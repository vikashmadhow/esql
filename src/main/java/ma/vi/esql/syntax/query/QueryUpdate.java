/*
 * Copyright (c) 2020-2021 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.exec.ColumnMapping;
import ma.vi.esql.exec.Result;
import ma.vi.esql.semantic.type.Relation;
import ma.vi.esql.semantic.type.Selection;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.*;
import ma.vi.esql.syntax.expression.*;
import ma.vi.esql.syntax.expression.literal.Literal;
import ma.vi.esql.syntax.expression.logical.And;
import ma.vi.esql.syntax.macro.Macro;
import ma.vi.esql.syntax.modify.InsertRow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.lang.System.Logger.Level.ERROR;
import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static ma.vi.base.string.Strings.random;
import static ma.vi.esql.translation.SqlServerTranslator.ADD_IIF;

/**
 * The parent of all query (select) and update (update, insert and delete)
 * ESQL statements.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class QueryUpdate extends MetadataContainer<QueryTranslation> {
  @SafeVarargs
  public QueryUpdate(Context context, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public QueryUpdate(QueryUpdate other) {
    super(other);
    this.type = other.type == null ? null : other.type.copy();
  }

  @SafeVarargs
  public QueryUpdate(QueryUpdate other,
                     String value,
                     T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract QueryUpdate copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract QueryUpdate copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  public Selection computeType(EsqlPath path) {
    if (type == Types.UnknownType) {
      Selection sel = new Selection(new ArrayList<>(columns()), null, tables(), null);
      if (path.hasAncestor(Macro.OngoingMacroExpansion.class)) {
        return sel;
      } else {
        type = sel;
      }
    }
    return (Selection)type;
  }

  /**
   * Returns the QueryUpdate ancestor in the path or, if not found, the Select
   * inside a SelectExpression in the ancestor path, if found. Otherwise returns
   * null. This utility method is useful to find the query-type of ancestor on
   * the path, normalizing how both QueryUpdate and SelectExpression are treated.
   */
  public static QueryUpdate ancestor(EsqlPath path) {
    Esql<?, ?> closest = path.closestAncestor(QueryUpdate.class, SelectExpression.class);
    return closest instanceof QueryUpdate q      ? q
         : closest instanceof SelectExpression s ? s.select()
         : null;
  }

  /**
   * Same as {@link #ancestor} but including the path.
   */
  public static T2<QueryUpdate, EsqlPath> ancestorAndPath(EsqlPath path) {
    T2<Esql<?, ?>, EsqlPath> closest = path.closestAncestorAndPath(QueryUpdate.class, SelectExpression.class);
    return closest == null                          ? null
         : closest.a instanceof QueryUpdate q       ? T2.of(q, closest.b)
         : closest.a instanceof SelectExpression s  ? T2.of(s.select(), closest.b)
         : null;
  }

  /**
   * @return Whether this is a grouping query or not. In grouping queries
   *         column expansions must be done carefully (or not at all) as they
   *         can affect the result of the query.
   */
  protected boolean grouped() {
    return false;
  }

  /**
   * @return Whether this is a modifying query (update, insert, delete) or not.
   */
  public abstract boolean modifying();

  /**
   * <p>
   * Adds the output clause to the query. For selects this is the selected columns. For
   * insert, update and delete this is the output clause (returning section for PostgreSql,
   * output for SQL Server).
   * </p>
   *
   * <p>
   * The following parameters in the parameters map are used in this function:
   * <ul>
   * <li><b>addAttributes:</b> Whether to add attributes (metadata) to the query result or not.</li>
   *
   * <li><b>optimiseAttributesLoading:</b> Whether to optimise attributes loading or not. If optimised, attributes
   *                                  whose value can be determined at compile time (such as literals) will
   *                                  not be added to the query. However, for certain type of queries, such as
   *                                  unions combining several queries, the optimisation for column attributes
   *                                  assume that the value is the same for all rows but this might not be the
   *                                  case for unions, as each separate query could provide different values
   *                                  for the same attributes.
   *                                  </li>
   * </ul>
   *
   * @param query                     The generated query result is added to this string builder.
   * @param target                    The target database being translated to, such as Postgresql and Sql server.
   * @param qualifier                 The qualifier to apply to the column references in the columns expressions
   *                                  of the result.
   * @return The query translation along with supporting information for its execution.
   */
  public QueryTranslation constructResult(StringBuilder query,
                                          Target target,
                                          EsqlPath path,
                                          String qualifier,
                                          Map<String, Object> parameters) {

    boolean addAttributes = (Boolean)parameters.getOrDefault("addAttributes", true);
    boolean optimiseAttributesLoading = (Boolean)parameters.getOrDefault("optimiseAttributesLoading", true);

    /*
     * If this query is part of another query, it is a subquery and its attributes
     * must not be optimised away (removed from the query and calculated statically)
     * as they could be referenced in the outer query. The single exception is
     * when the query is the last query in a `With` query; in that case, attributes
     * loading can be optimised as the columns will not be referred outside of
     * the `With` query.
     */
    if (path.tail() != null) {
      QueryUpdate qu = path.tail().ancestor(QueryUpdate.class);
      if (qu != null && !(qu instanceof With w && w.query() == this)) {
        optimiseAttributesLoading = false;
      }
    }

    Map<String, ColumnMapping> columnMappings = new LinkedHashMap<>();
    Map<String, Object> resultAttributes = new LinkedHashMap<>();
    List<AttributeIndex> resultAttributeIndices = new ArrayList<>();
    int itemIndex = 0;
    Selection selection = computeType(path.add(this));

    /*
     * Output columns.
     */
    for (T2<Relation, Column> c: selection.columns().stream()
                                          .filter(c -> !c.b.name().contains("/"))
                                          .toList()) {
      itemIndex++;
      if (itemIndex > 1) {
        query.append(", ");
      }
      Column column = c.b;
      query.append(column.translate(target, path.add(column), ADD_IIF));
      String colName = column.name();
      columnMappings.put(colName, new ColumnMapping(itemIndex,
                                                    column,
                                                    column.computeType(path.add(column)),
                                                    new ArrayList<>(),
                                                    new HashMap<>()));
    }

    /*
     * Do not expand column list of selects inside expressions as the whole
     * expression is a single-value and expanding the column list will break the
     * query or not be of any use. The same applies to when select is used as an
     * insert value, or part of a column list.
     */
    if (!path.hasAncestor(SelectExpression.class,
                          InsertRow       .class,
                          Column          .class)) {
      /*
       * Output result metadata
       */
      for (T2<Relation, Column> column: selection.columns().stream()
                                                 .filter(c -> c.b.name().startsWith("/")).toList()) {
        String colName = column.b.name();
        String attrName = colName.substring(1);

        Expression<?, String> attributeValue = column.b.expression();
        if (optimiseAttributesLoading && (attributeValue instanceof Literal
                                       || attributeValue instanceof UncomputedExpression)) {
          resultAttributes.put(attrName, attributeValue.value(target, path));
        } else if (addAttributes) {
          itemIndex += 1;
          if (itemIndex > 1) {
            query.append(", ");
          }
          appendExpression(query, attributeValue, target, path, colName);
          resultAttributeIndices.add(new AttributeIndex(itemIndex, attrName, attributeValue.computeType(path.add(attributeValue))));
        }
      }

      /*
       * Output columns metadata.
       */
      for (T2<Relation, Column> col: selection.columns().stream()
                                              .filter(c -> c.b.name().charAt(0) != '/' && c.b.name().indexOf('/', 1) != -1)
                                              .toList()) {
        String alias = col.b.name();
        int pos = alias.indexOf('/');
        String colName = alias.substring(0, pos);
        ColumnMapping mapping = columnMappings.get(colName);
        if (mapping == null) {
          throw new RuntimeException("Could not find column mapping for column " + colName
                                   + " while there is an attribute for that columns (" + alias + ")");
        }

        String attrName = alias.substring(pos + 1);
        Expression<?, String> attributeValue = col.b.expression();
        if (optimiseAttributesLoading &&  (attributeValue instanceof Literal
                                        || attributeValue instanceof UncomputedExpression)) {
          mapping.attributes().put(attrName, attributeValue.value(target, path));
        } else if (addAttributes) {
          itemIndex += 1;
          query.append(", ");
          appendExpression(query, attributeValue, target, path, alias);
          mapping.attributeIndices().add(new AttributeIndex(itemIndex, attrName, attributeValue.computeType(path.add(attributeValue))));
        }
      }
    }
    return new QueryTranslation(this, query.toString(),
                                unmodifiableList(new ArrayList<>(columnMappings.values())),
                                unmodifiableList(resultAttributeIndices),
                                unmodifiableMap(resultAttributes));
  }

  /**
   * Appends the expression to the output clause of this query.
   */
  protected void appendExpression(StringBuilder query,
                                  Expression<?, String> expression,
                                  Target target,
                                  EsqlPath path,
                                  String alias) {
    query.append(expression.translate(target, path.add(expression), ADD_IIF));
    if (alias != null) {
      query.append(" \"").append(alias).append('"');
    }
  }

  /**
   * A name for this query; only common-table-expressions (CTE) have meaningful name
   * which can be referred to in other part of the query; in all other cases a randomly-
   * generated name is sufficient (and the default).
   */
  protected String name() {
    return "Projection_" + random(2);
  }

  /**
   * The list of columns being returned by this query or update.
   * For a query (select) this will be the list of expressions being
   * selected; for updates this will be the list of returning columns
   * if any.
   */
  public List<Column> columns() {
    return child("columns").children();
  }

  /**
   * Returns the columns embedded in its Esql envelop.
   */
  public ColumnList columnList() {
    return child("columns");
  }

  /**
   * Returns the table expression for selection or updates.
   */
  public TableExpr tables() {
    return child("tables");
  }

  public TableExpr from() {
    return tables();
  }

  public Expression<?, String> where() {
    return child("where");
  }

  public QueryUpdate where(Expression<?, String> where) {
    return where(where, false);
  }

  public <T extends QueryUpdate> T where(Expression<?, String> where, boolean addToJoin) {
    if (addToJoin) {
      /*
       * Add to the join list if there is one.
       */
      TableExpr tables = tables();
      if (tables instanceof AbstractJoinTableExpr) {
        /*
         * Get aliases in the where clause to ensure that
         * they are all included in the joins.
         */
        Set<String> qualifiers = new HashSet<>();
        where.forEach((e, path) -> {
          if (e instanceof ColumnRef && ((ColumnRef)e).qualifier() != null) {
            qualifiers.add(((ColumnRef)e).qualifier());
          }
          return true;
        });

        List<Join> joinedTables = new ArrayList<>();
        lineariseJoins(tables(), joinedTables);

        Set<String> aliases = new HashSet<>();
        for (Join join: joinedTables) {
          String alias = join.table.alias();
          if (alias != null) {
            aliases.add(alias);
          }

          if (join.joinType != null) {
            /*
             * Don't add to join if there's an outer join as such a join is not
             * restrictive.
             */
            break;
          }
          if (aliases.containsAll(qualifiers)) {
            return replace(join.on, new And(context, join.on, new GroupedExpression(context, where)));
          }
        }
      }
    }
    /*
     * Add as where clause.
     */
    Expression<?, String> w = where();
    return set("where", w == null ? where
                                  : new And(context,
                                            new GroupedExpression(context, w),
                                            new GroupedExpression(context, where)));
  }

  public QueryUpdate where(String where) {
    return where(new Parser(context.structure).parseExpression(where));
  }

  protected static void lineariseJoins(TableExpr tables,
                                       List<Join> joins) {
    if (tables instanceof AbstractJoinTableExpr join) {
      lineariseJoins(join.left(), joins);
      TableExpr right = join.right();
      lineariseJoins(right, joins);
      if (join instanceof JoinTableExpr jt
       && right instanceof SingleTableExpr single) {
        joins.add(new Join(jt.joinType(), single, jt.on()));
      }
    }
  }

  @Override
  public Result execute(Database db, Connection con, EsqlPath path) {
    QueryTranslation q = translate(db.target(), path);
    try {
      Selection selection = computeType(path.add(this));
      if (!selection.columns().isEmpty()) {
        ResultSet rs = con.createStatement(TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY)
                          .executeQuery(q.translation());
        return new Result(db, rs, q);
      } else {
        con.createStatement().executeUpdate(q.translation());
        return new Result(db, null, q);
      }
    } catch (SQLException e) {
      log.log(ERROR, e.getMessage());
      log.log(ERROR, "Original query: " + this);
      log.log(ERROR, "Translation: " + q.translation());
      throw new RuntimeException(e);
    }
  }

  protected record Join(String                joinType,
                        SingleTableExpr       table,
                        Expression<?, String> on) {}

//  public T2<Boolean, String> restrict(String restrictToTable) {
//    return restrict(context.structure.relation(restrictToTable));
//  }
//
//  public T2<Boolean, String> restrict(Relation restrictToTable) {
//    return restrict(restrictToTable, true);
//  }
//
//  public T2<Boolean, String> restrict(Relation restrictToTable,
//                                      boolean ignoreHiddenFields) {
//    return restrict(restrictToTable, null, ignoreHiddenFields, true);
//  }
//
//  public T2<Boolean, String> restrict(Relation restrictToTable,
//                                      boolean ignoreHiddenFields,
//                                      boolean followSubSelect) {
//    return restrict(restrictToTable, null, ignoreHiddenFields, followSubSelect);
//  }
//
//  public T2<Boolean, String> restrict(Relation restrictToTable,
//                                      String targetAlias,
//                                      boolean ignoreHiddenFields,
//                                      boolean followSubSelect) {
//    return restrict(new Restriction(restrictToTable), targetAlias, ignoreHiddenFields, followSubSelect);
//  }
//
//  public T2<Boolean, String> restrict(Restriction restriction,
//                                      String targetAlias,
//                                      boolean ignoreHiddenFields,
//                                      boolean followSubSelect) {
//    TableExpr selectFrom = tables();
//    if (selectFrom instanceof SelectTableExpr) {
//      if (followSubSelect) {
//        return ((SelectTableExpr)selectFrom).select()
//                                            .restrict(restriction, targetAlias, ignoreHiddenFields, followSubSelect);
//      } else {
//        return T2.of(false, null);
//      }
//    } else {
//      boolean joined = false;
//      String alias = null;
//
//      List<T2<ForeignKeyConstraint, Boolean>> shortestPath = null;
//      SingleTableExpr shortestPathSource = null;
//      Restriction.By by = null;
//
//      restrict:
//      for (Iterator<Restriction.By> i = restriction.by.iterator(); i.hasNext(); ) {
//        by = i.next();
//        if (selectFrom instanceof SingleTableExpr singleTableExpr) {
//          if (singleTableExpr.tableName().equals(by.table.name())) {
//            /*
//             * The from clause is the restricted table, no need to search
//             * for other restrictions as this is the shortest possible path.
//             */
//            shortestPathSource = singleTableExpr;
//            shortestPath = null;
//            break;
//          } else {
//            /*
//             * find a path to the restricted table if any;
//             * keep only the shortest path.
//             */
//            BaseRelation sourceTable = context.structure.relation(singleTableExpr.tableName());
//            if (sourceTable != null) {
//              List<T2<ForeignKeyConstraint, Boolean>> path = sourceTable.path(by.table, ignoreHiddenFields);
//              if (!path.isEmpty() && (shortestPath == null || path.size() < shortestPath.size())) {
//                shortestPath = path;
//                shortestPathSource = singleTableExpr;
//              }
//            }
//          }
//        } else if (selectFrom instanceof AbstractJoinTableExpr join) {
//          /*
//           * Perform a BFS over the branches of a join tree to find
//           * the shortest path from one of the table in the join to
//           * the restricted table.
//           */
//          Queue<AbstractJoinTableExpr> toExplore = new ArrayDeque<>();
//          toExplore.add(join);
//
//          while (!toExplore.isEmpty()) {
//            join = toExplore.remove();
//            TableExpr[] branches = new TableExpr[]{join.left(), join.right()};
//            for (TableExpr tableExpr: branches) {
//              if (tableExpr instanceof SingleTableExpr singleTableExpr) {
//                String tableName = singleTableExpr.tableName();
//                if (tableName.equals(by.table.name())) {
//                  /*
//                   * One of the joined table is the restricted table, no need to search
//                   * for other restrictions as this is the shortest possible path.
//                   */
//                  shortestPathSource = singleTableExpr;
//                  shortestPath = null;
//                  break restrict;
//                } else {
//                  BaseRelation source = context.structure.relation(tableName);
//                  if (source != null) {
//                    List<T2<ForeignKeyConstraint, Boolean>> path = source.path(by.table, ignoreHiddenFields);
//                    if (!path.isEmpty() && (shortestPath == null || path.size() < shortestPath.size())) {
//                      shortestPath = path;
//                      shortestPathSource = singleTableExpr;
//                    }
//                  }
//                }
//              } else if (tableExpr instanceof SelectTableExpr) {
//                if (followSubSelect) {
//                  ((SelectTableExpr)tableExpr).select()
//                                              .restrict(restriction, targetAlias, ignoreHiddenFields, followSubSelect);
//                }
//              } else if (tableExpr instanceof AbstractJoinTableExpr) {
//                toExplore.add((AbstractJoinTableExpr)tableExpr);
//              }
//            }
//          }
//        }
//      }
//      if (shortestPathSource != null) {
//        joined = true;
//        if (shortestPath != null && !shortestPath.isEmpty()) {
//          boolean hasReverseLink = shortestPath.stream().anyMatch(T2::b);
//          if (hasReverseLink && this instanceof Select) {
//            /*
//             * Joining a table along a reverse foreign key path may result
//             * in duplicates in the source table records (consequence of
//             * joining along one-to-many relationships paths). These duplicates
//             * can be removed by selecting only distinct records.
//             */
//            ((Select)this).distinct(true);
//          }
//          String sourceAlias = shortestPathSource.alias();
//          TableExpr toReplace = shortestPathSource.parent() instanceof JoinTableExpr
//                                ? (JoinTableExpr)shortestPathSource.parent()
//                                : shortestPathSource;
//          TableExpr pathJoin = toReplace.copy();
//          for (int i = 0; i < shortestPath.size(); i++) {
//            T2<ForeignKeyConstraint, Boolean> l = shortestPath.get(i);
//            ForeignKeyConstraint link = l.a;
//            boolean reversePath = l.b;
//
//            if (i == shortestPath.size() - 1) {
//              alias = targetAlias == null ? "t" + Strings.random() : targetAlias;
//            } else {
//              alias = "t" + Strings.random();
//            }
//
//            /*
//             * Links can be in reverse path. (foreign key from target to source).
//             * E.g. Com c <- Emp e: from Com c join Emp t on c._id (target field in link)=e.com_id (source fields in link)
//             * Or normal path (foreign key from source to target).
//             * E.g. Emp e -> Com c: from Emp e join Com t on e.company_id=t._id
//             */
//            Expression<?, String> on = new Equality(context,
//                                                    new ColumnRef(context,
//                                                          sourceAlias,
//                                                          reversePath ? link.targetColumns().get(0)
//                                                                      : link.sourceColumns().get(0)),
//                                                    new ColumnRef(context,
//                                                          alias,
//                                                          reversePath ? link.sourceColumns().get(0)
//                                                                      : link.targetColumns().get(0)));
//            if (link.sourceColumns().size() > 1) {
//              for (int j = 1; j < link.sourceColumns().size(); j++) {
//                on = new And(context,
//                             on,
//                             new Equality(context,
//                                                 new ColumnRef(context,
//                                                               sourceAlias,
//                                                               reversePath ? link.targetColumns().get(j)
//                                                                           : link.sourceColumns().get(j)),
//                                                 new ColumnRef(context,
//                                                               alias,
//                                                               reversePath ? link.sourceColumns().get(j)
//                                                                           : link.targetColumns().get(j))));
//              }
//            }
//            if (i == shortestPath.size() - 1
//                && by.column != null
//                && restriction.values != null
//                && !restriction.values.isEmpty()) {
//              /*
//               * Add restrict clause on the last part of the path
//               * (which is the target restricted table).
//               */
//              on = new And(context,
//                           on,
//                           restrictClause(context,
//                                                 reversePath ? link.table() : link.targetTable(),
//                                                 alias,
//                                                 restriction.excluded,
//                                                 by.column,
//                                                 restriction.values));
//            }
//            pathJoin = new JoinTableExpr(context,
//                                         pathJoin,
//                                         null,
//                                         new SingleTableExpr(context,
//                                                             reversePath ? link.table() : link.targetTable(),
//                                                             alias),
//                                         on);
//            sourceAlias = alias;
//          }
//          toReplace.replaceWith(pathJoin);
//          List<Column> cols = columns();
//          if (cols != null && cols.size() == 1) {
//            Column singleCol = cols.get(0);
//            if (String.valueOf(singleCol.expression()).equals("*")) {
//              columns(shortestPathSource.alias() + ".*");
//            }
//          }
//          type = null;
//        } else {
//          /*
//           * The shortestPathSource is the table to restrict (there's no need for a path).
//           */
//          alias = shortestPathSource.alias();
//          if (by.column != null
//              && restriction.values != null
//              && !restriction.values.isEmpty()) {
//            Expression<?, String> restrict = restrictClause(context,
//                                                            shortestPathSource.tableName(),
//                                                            shortestPathSource.alias(),
//                                                            restriction.excluded,
//                                                            by.column,
//                                                            restriction.values);
//            where(where() == null
//                  ? restrict
//                  : new And(context, where(), restrict));
//          }
//        }
//      }
//      return T2.of(joined, alias);
//    }
//  }
//
//  private static Expression<?, String> restrictClause(Context context,
//                                                      String restrictTableName,
//                                                      String restrictTableAlias,
//                                                      boolean exclude,
//                                                      String restrictColumn,
//                                                      Set<String> restrictValues) {
//    if (restrictColumn == null
//        || restrictValues == null
//        || restrictValues.isEmpty()) {
//      var one = new IntegerLiteral(context, 1L);
//      return new Equality(context, one, one);
//    } else {
//      BaseRelation relation = context.structure.relation(restrictTableName);
//      Column column = relation.column(restrictColumn);
//      return new In(context,
//                    new ColumnRef(context, restrictTableAlias, restrictColumn),
//                    exclude,
//                    restrictValues.stream()
//                                  .map(v -> Literal.makeLiteral(context, v, column.type(new EsqlPath(column))))
//                                  .collect(toList()));
//    }
//  }

  private static final System.Logger log = System.getLogger(QueryUpdate.class.getName());
}