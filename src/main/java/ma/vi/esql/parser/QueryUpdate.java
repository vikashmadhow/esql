/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser;

import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.base.tuple.T3;
import ma.vi.esql.builder.Attr;
import ma.vi.esql.database.Structure;
import ma.vi.esql.exec.ColumnMapping;
import ma.vi.esql.exec.Result;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.define.ForeignKeyConstraint;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.expression.*;
import ma.vi.esql.parser.query.*;
import ma.vi.esql.type.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.lang.System.Logger.Level.ERROR;
import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static ma.vi.base.string.Strings.makeUnique;
import static ma.vi.base.string.Strings.random;
import static ma.vi.esql.parser.Translatable.Target.ESQL;

/**
 * The parent of all query (select) and update (update, insert and delete)
 * ESQL statements.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class QueryUpdate extends MetadataContainer<String, QueryTranslation> implements Macro {
  public QueryUpdate(Context context,
                     String value,
                     T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public QueryUpdate(QueryUpdate other) {
    super(other);
    this.type = other.type == null ? null : other.type.copy();
  }

  @Override
  public abstract QueryUpdate copy();

  @Override
  public int expansionOrder() {
    return HIGHEST;
  }

  @Override
  public boolean expand(String name, Esql<?, ?> esql) {
    /*
     * Expand all columns (*) to the individual columns they refer to
     */
    TableExpr from = tables();
    Relation fromType = from.type();

    boolean expanded = false;
    List<Column> resolvedColumns = new ArrayList<>();
    for (Column column: columns()) {
      if (column instanceof StarColumn) {
        expanded = true;
        StarColumn all = (StarColumn)column;
        String qualifier = all.qualifier();
        Relation rel = qualifier == null ? fromType : fromType.forAlias(qualifier);
        for (Column relCol: rel.columns()) {
          Column col = new Column(context, relCol.alias(), new ColumnRef(context, qualifier, relCol.alias()), null);
          resolvedColumns.add(col);
        }
      } else {
        resolvedColumns.add(column);
      }
    }
    columns(resolvedColumns);
    return expanded;
  }

  @Override
  public Selection type() {
    if (type == null) {
      TableExpr from = tables();
      Relation fromType = from.type();

      Map<String, Column> columns = new LinkedHashMap<>();
      Set<String> columnNames = new HashSet<>();
      Map<String, String> aliased = new HashMap<>();

      if (!grouped() && !modifying()) {
        /*
         * Add all relation-level metadata columns defined on the tables
         * being queried.
         */
        for (Column column: fromType.columnsPrefixedBy(null, "/")) {
          columns.put(column.alias(), column.copy());
          columnNames.add(column.alias());
        }

        /*
         * Override with explicit result metadata defined in query.
         */
        Metadata metadata = metadata();
        if (metadata != null && metadata.attributes() != null) {
          for (Attribute a: metadata.attributes().values()) {
            for (Column column: BaseRelation.columnsForAttribute(a, "/", emptyMap())) {
              columns.put(column.alias(), column.copy());
              columnNames.add(column.alias());
            }
          }
        }
      }

      // Add columns and metadata
      int columnNumber = 1;
      if (columns() != null) {
        for (Column column: columns()) {
          Expression<?> colExpr = column.expr();
          String alias = column.alias();
          if (colExpr instanceof ColumnRef) {
            ColumnRef ref = (ColumnRef)colExpr;
            String refName = ref.name();
            if (!refName.equals(alias)) {
              aliased.put(refName, alias);
            }
            if (!grouped()) {
              for (Column col: fromType.columnsPrefixedBy(ref.qualifier(), refName)) {
                col = col.copy();
                String colAlias = col.alias();
                if (colAlias == null) {
                  col.alias(alias);
                } else {
                  col.alias(alias + colAlias.substring(ref.name().length()));
                }
                columns.put(col.alias(), col);
                columnNames.add(col.alias());
              }
            } else {
              if (alias == null) {
                alias = "column" + columnNumber;
              }
              alias = makeUnique(columnNames, alias);
              column = column.copy();
              column.alias(alias);
              columns.put(column.alias(), column);
              columnNames.add(column.alias());
            }
          } else {
            if (alias == null) {
              alias = "column" + columnNumber;
            }
            alias = makeUnique(columnNames, alias);
            column = column.copy();
            column.alias(alias);
            columns.put(alias, column);
            columnNames.add(alias);
          }

          // Override with explicit result metadata defined in select
          if (!grouped()) {
            if (column.metadata() != null && column.metadata().attributes() != null) {
              for (Attribute a: column.metadata().attributes().values()) {
                for (Column c: BaseRelation.columnsForAttribute(a, alias, aliased)) {
                  columns.put(column.alias(), column.copy());
                  columnNames.add(column.alias());
                }
              }
            }
          }
        }
      }

      // replace column names with their aliases in uncomputed forms intended
      // to be run on client-side
      if (!aliased.isEmpty()) {
        for (Column c: columns.values()) {
          if (c.expr() instanceof UncomputedExpression) {
            BaseRelation.rename(c.expr(), aliased);
          }
        }
      }
      type = new Selection(new ArrayList<>(columns.values()), from);
      context.type(type);
    }
    return type;
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
   * Adds the output clause to the query. For selects this is the selected columns. For
   * insert, update and delete this is the output clause (returning section for PostgreSql,
   * output for SQL Server).
   *
   * @param query                     The generated query result is added to this string builder.
   * @param target                    The target database being translated to, such as Postgresql and Sql server.
   * @param qualifier                 The qualifier to apply to the column references in the columns expressions
   *                                  of the result.
   * @param addAttributes             Whether to add attributes (metadata) to the query result or not.
   * @param optimiseAttributesLoading Whether to optimise attributes loading or not. If optimised, attributes
   *                                  whose value can be determined at compile time (such as literals) will
   *                                  not be added to the query. However, for certain type of queries, such as
   *                                  unions combining several queries, the optimisation for column attributes
   *                                  assume that the value is the same for all rows but this might not be the
   *                                  case for unions, as each separate query could provide different values
   *                                  for the same attributes.
   * @return The query translation along with supporting information for its execution.
   */
  public QueryTranslation constructResult(StringBuilder query,
                                          Target target,
                                          String qualifier,
                                          boolean addAttributes,
                                          boolean optimiseAttributesLoading) {
//    /*
//     * Do not expand column list of selects inside expressions as the
//     * whole expression is a single-value and expanding the column list
//     * will break the query or not be of any use.
//     */
//    boolean selectExpression = ancestor(SelectExpression.class) != null;

    Map<String, Integer> columnToIndex = new HashMap<>();
    Map<String, ColumnMapping> columnMappings = new LinkedHashMap<>();
    Map<String, Object> resultAttributes = new HashMap<>();
    List<T3<Integer, String, Type>> resultAttributeIndices = new ArrayList<>();
    int itemIndex = 0;
    Selection selection = type();

    /*
     * Output result metadata
     */
    for (Column column: selection.columns()
                                 .stream()
                                 .filter(c -> c.alias().startsWith("/"))
                                 .collect(toList())) {
      String colName = column.alias();
      String attrName = colName.substring(1);
      columnToIndex.put(colName, itemIndex);

      Expression<?> attributeValue = column.expr();
      if (optimiseAttributesLoading && attributeValue instanceof Literal) {
        resultAttributes.put(attrName, attributeValue.value(target));
      } else if (addAttributes) {
        itemIndex += 1;
        if (itemIndex > 1) {
          query.append(", ");
        }
        appendExpression(query, attributeValue, target, qualifier);
        resultAttributeIndices.add(T3.of(itemIndex, attrName, attributeValue.type()));
      }
    }

    /*
     * Output columns.
     */
    for (Column column: selection.columns()
                                 .stream()
                                 .filter(c -> !c.alias().contains("/"))
                                 .collect(toList())) {
      itemIndex++;
      if (itemIndex > 1) {
        query.append(", ");
      }
      column = column.copy();
      Expression<?> expression = column.expr();
      if (qualifier != null) {
        ColumnRef.qualify(expression, qualifier, null, true);
      }
      query.append(column.translate(target));
      String colName = column.alias();
      columnToIndex.put(colName, itemIndex);
      columnMappings.put(colName, new ColumnMapping(itemIndex,
                                                    column.type(),
                                                    new ArrayList<>(),
                                                    new HashMap<>()));
    }

    /*
     * Output columns metadata.
     */
    for (Column col: selection.columns()
                              .stream()
                              .filter(c -> c.alias().indexOf('/', 1) != -1)
                              .collect(toList())) {
      String alias = col.alias();
      int pos = alias.indexOf('/');
      String colName = alias.substring(0, pos);
      ColumnMapping mapping = columnMappings.get(colName);
      if (mapping == null) {
        throw new RuntimeException("Could not find column mapping for column " + colName
                                 + " while there is an attribute for that columns (" + alias + ")");
      }

      String attrName = alias.substring(pos + 1);
      Expression<?> attributeValue = col.expr();
      if (optimiseAttributesLoading && attributeValue instanceof Literal) {
        mapping.attributes.put(attrName, attributeValue.value(target));
      } else if (addAttributes) {
        itemIndex += 1;
        query.append(", ");
        appendExpression(query, attributeValue, target, qualifier);
        mapping.attributeIndices.add(T3.of(itemIndex, attrName, attributeValue.type()));
      }
    }
    return new QueryTranslation(query.toString(),
                                new ArrayList<>(columnMappings.values()),
                                columnToIndex,
                                resultAttributeIndices,
                                resultAttributes);
  }

  /**
   * Appends the expression to the output clause of this query.
   */
  protected void appendExpression(StringBuilder query,
                                  Expression<?> expression,
                                  Target target,
                                  String qualifier) {
    if (qualifier != null) {
      ColumnRef.qualify(expression, qualifier, null, true);
    }
    Type type = expression.type();
    if (target == Target.SQLSERVER && type == Types.BoolType) {
      /*
       * SQL Server does not have a boolean type; work-around using an IIF
       */
      String boolValue = expression.translate(target);
      if (boolValue.equals("0") || boolValue.equals("1")) {
        /*
         * a boolean literal was specified: no need to use IIF
         */
        query.append(boolValue);
      } else {
        /*
         * otherwise, turn into a boolean expression using IIF
         */
        query.append("iif(").append(boolValue).append(", 1, 0)");
      }
    } else {
      query.append(expression.translate(target));
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
    return childValue("columns");
  }

  /**
   * Returns the columns embedded in its Esql envelop.
   */
  public Esql<List<Column>, ?> columnsAsEsql() {
    return child("columns");
  }

  public void columns(List<Column> columns) {
    child("columns", new Esql<>(context, columns));
    type = null;
  }

  public void columns(T3<String, String, List<Attr>>... columns) {
    List<Column> cols = new ArrayList<>();
    Parser parser = new Parser(context.structure);
    for (var column: columns) {
      cols.add(new Column(
          context,
          column.b,
          parser.parseExpression(column.a),
          column.c == null
          ? null
          : new Metadata(context,
                         column.c.stream()
                                 .map(a -> new Attribute(context,
                                                         a.name,
                                                         parser.parseExpression(a.expr)))
                                 .collect(toList()))));
    }
    columns(cols);
  }

  public void columns(String... columns) {
    Parser parser = new Parser(context.structure);
    List<Column> cols = new ArrayList<>();
    for (var column: columns) {
      cols.add(new Column(context,
                          null,
                          parser.parseExpression(column),
                          null));
    }
    columns(cols);
  }

  public String columnsAsString() {
    return columns().stream()
                    .map(c -> c.translate(ESQL))
                    .collect(joining(", "));
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

  public Expression<?> where() {
    return child("where");
  }

  public QueryUpdate where(Expression<?> where) {
    return where(where, false);
  }

  public QueryUpdate where(Expression<?> where, boolean addToJoin) {
    boolean added = false;
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
        where.forEach(e -> {
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
             * Don't add to join if there's an outer join
             * as such a join is not restrictive.
             */
            break;
          }
          if (aliases.containsAll(qualifiers)) {
            join.on.replaceWith(
                new LogicalAnd(
                    where.context,
                    join.on.copy(),
                    new GroupedExpression(where.context, where)
                )
            );
            added = true;
            break;
          }
        }
      }
    }
    if (!added) {
      /*
       * Add as where clause.
       */
      Expression<?> w = where();
      child("where",
            w == null
              ? where
              : new LogicalAnd(context,
                               new GroupedExpression(context, w),
                               new GroupedExpression(context, where)));
    }
    return this;
  }

  public QueryUpdate where(String where) {
    return where(new Parser(context.structure).parseExpression(where));
  }

  protected static void lineariseJoins(TableExpr tables,
                                       List<Join> joins) {
    if (tables instanceof AbstractJoinTableExpr) {
      AbstractJoinTableExpr join = (AbstractJoinTableExpr)tables;
      lineariseJoins(join.left(), joins);

      TableExpr right = join.right();
      lineariseJoins(right, joins);
      if (join instanceof JoinTableExpr
          && right instanceof SingleTableExpr) {
        JoinTableExpr jt = (JoinTableExpr)join;
        SingleTableExpr single = (SingleTableExpr)right;
        joins.add(new Join(jt.joinType(), single, jt.on()));
      }
    }
  }

  @Override
  public Result execute(Connection con, Structure structure, Target target) {
    Selection selection = type();
    QueryTranslation translation = translate(target);
    try {
      if (!selection.columns().isEmpty()) {
        ResultSet rs = con.createStatement(TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY)
                          .executeQuery(translation.statement);
        return new Result(structure, rs,
                          selection,
                          translation.columns,
                          translation.columnToIndex,
                          translation.resultAttributeIndices,
                          translation.resultAttributes);
      } else {
        con.createStatement().executeUpdate(translation.statement);
        return new Result(structure, null,
                          selection,
                          translation.columns,
                          translation.columnToIndex,
                          translation.resultAttributeIndices,
                          translation.resultAttributes);
      }
    } catch (SQLException e) {
      log.log(ERROR, e.getMessage());
      log.log(ERROR, "Original query: " + this);
      log.log(ERROR, "Translation: " + translation.statement);
      throw new RuntimeException(e);
    }
  }

  protected static class Join {
    public Join(String joinType, SingleTableExpr table, Expression<?> on) {
      this.joinType = joinType;
      this.table = table;
      this.on = on;
    }

    public final String joinType;
    public final SingleTableExpr table;
    public final Expression<?> on;
  }

  protected static void linearise(TableExpr expr,
                                  List<TableExpr> linearisedTables,
                                  List<Expression<?>> joinExpressions) {
    if (expr instanceof AbstractJoinTableExpr) {
      AbstractJoinTableExpr join = (AbstractJoinTableExpr)expr;
      linearise(join.left(), linearisedTables, joinExpressions);
      linearise(join.right(), linearisedTables, joinExpressions);
      if (expr instanceof JoinTableExpr) {
        joinExpressions.add(((JoinTableExpr)expr).on());
      }
    } else {
      linearisedTables.add(expr);
    }
  }

  @Override
  public void close() {
    if (!closed() && !closing()) {
      try {
        closing(true);
        if (type != null) {
          type.close();
        }
      } finally {
        closing(false);
        closed(true);
      }
      super.close();
    }
  }


  public T2<Boolean, String> restrict(String restrictToTable) {
    return restrict(context.structure.relation(restrictToTable));
  }

  public T2<Boolean, String> restrict(Relation restrictToTable) {
    return restrict(restrictToTable, true);
  }

  public T2<Boolean, String> restrict(Relation restrictToTable,
                                      boolean ignoreHiddenFields) {
    return restrict(restrictToTable, null, ignoreHiddenFields, true);
  }

  public T2<Boolean, String> restrict(Relation restrictToTable,
                                      boolean ignoreHiddenFields,
                                      boolean followSubSelect) {
    return restrict(restrictToTable, null, ignoreHiddenFields, followSubSelect);
  }

  public T2<Boolean, String> restrict(Relation restrictToTable,
                                      String targetAlias,
                                      boolean ignoreHiddenFields,
                                      boolean followSubSelect) {
    return restrict(new Restriction(restrictToTable), targetAlias, ignoreHiddenFields, followSubSelect);
  }

  public T2<Boolean, String> restrict(Restriction restriction,
                                      String targetAlias,
                                      boolean ignoreHiddenFields,
                                      boolean followSubSelect) {
    TableExpr selectFrom = tables();
    if (selectFrom instanceof SelectTableExpr) {
      if (followSubSelect) {
        return ((SelectTableExpr)selectFrom).select()
                                            .restrict(restriction, targetAlias, ignoreHiddenFields, followSubSelect);
      } else {
        return T2.of(false, null);
      }
    } else {
      boolean joined = false;
      String alias = null;

      List<T2<ForeignKeyConstraint, Boolean>> shortestPath = null;
      SingleTableExpr shortestPathSource = null;
      Restriction.By by = null;

      restrict:
      for (Iterator<Restriction.By> i = restriction.by.iterator(); i.hasNext(); ) {
        by = i.next();
        if (selectFrom instanceof SingleTableExpr) {
          SingleTableExpr singleTableExpr = (SingleTableExpr)selectFrom;
          if (singleTableExpr.tableName().equals(by.table.name())) {
            /*
             * The from clause is the restricted table, no need to search
             * for other restrictions as this is the shortest possible path.
             */
            shortestPathSource = singleTableExpr;
            shortestPath = null;
            break;
          } else {
            /*
             * find a path to the restricted table if any;
             * keep only the shortest path.
             */
            BaseRelation sourceTable = context.structure.relation(singleTableExpr.tableName());
            if (sourceTable != null) {
              List<T2<ForeignKeyConstraint, Boolean>> path = sourceTable.path(by.table, ignoreHiddenFields);
              if (!path.isEmpty() && (shortestPath == null || path.size() < shortestPath.size())) {
                shortestPath = path;
                shortestPathSource = singleTableExpr;
              }
            }
          }
        } else if (selectFrom instanceof AbstractJoinTableExpr) {
          /*
           * Perform a BFS over the branches of a join tree to find
           * the shortest path from one of the table in the join to
           * the restricted table.
           */
          AbstractJoinTableExpr join = (AbstractJoinTableExpr)selectFrom;
          Queue<AbstractJoinTableExpr> toExplore = new ArrayDeque<>();
          toExplore.add(join);

          while (!toExplore.isEmpty()) {
            join = toExplore.remove();
            TableExpr[] branches = new TableExpr[]{join.left(), join.right()};
            for (TableExpr tableExpr: branches) {
              if (tableExpr instanceof SingleTableExpr) {
                SingleTableExpr singleTableExpr = (SingleTableExpr)tableExpr;
                String tableName = singleTableExpr.tableName();
                if (tableName.equals(by.table.name())) {
                  /*
                   * One of the joined table is the restricted table, no need to search
                   * for other restrictions as this is the shortest possible path.
                   */
                  shortestPathSource = singleTableExpr;
                  shortestPath = null;
                  break restrict;
                } else {
                  BaseRelation source = context.structure.relation(tableName);
                  if (source != null) {
                    List<T2<ForeignKeyConstraint, Boolean>> path = source.path(by.table, ignoreHiddenFields);
                    if (!path.isEmpty() && (shortestPath == null || path.size() < shortestPath.size())) {
                      shortestPath = path;
                      shortestPathSource = singleTableExpr;
                    }
                  }
                }
              } else if (tableExpr instanceof SelectTableExpr) {
                if (followSubSelect) {
                  ((SelectTableExpr)tableExpr).select()
                                              .restrict(restriction, targetAlias, ignoreHiddenFields, followSubSelect);
                }
              } else if (tableExpr instanceof AbstractJoinTableExpr) {
                toExplore.add((AbstractJoinTableExpr)tableExpr);
              }
            }
          }
        }
      }
      if (shortestPathSource != null) {
        joined = true;
        if (shortestPath != null && !shortestPath.isEmpty()) {
          boolean hasReverseLink = shortestPath.stream().anyMatch(T2::b);
          if (hasReverseLink && this instanceof Select) {
            /*
             * Joining a table along a reverse foreign key path may result
             * in duplicates in the source table records (consequence of
             * joining along one-to-many relationships paths). These duplicates
             * can be removed by selecting only distinct records.
             */
            ((Select)this).distinct(true);
          }
          String sourceAlias = shortestPathSource.alias();
          TableExpr toReplace = shortestPathSource.parent() instanceof JoinTableExpr
                                ? (JoinTableExpr)shortestPathSource.parent()
                                : shortestPathSource;
          TableExpr pathJoin = toReplace.copy();
          for (int i = 0; i < shortestPath.size(); i++) {
            T2<ForeignKeyConstraint, Boolean> l = shortestPath.get(i);
            ForeignKeyConstraint link = l.a;
            boolean reversePath = l.b;

            if (i == shortestPath.size() - 1) {
              alias = targetAlias == null ? "t" + Strings.random() : targetAlias;
            } else {
              alias = "t" + Strings.random();
            }

            /*
             * Links can be in reverse path. (foreign key from target to source).
             * E.g. Com c <- Emp e: from Com c join Emp t on c._id (target field in link)=e.com_id (source fields in link)
             * Or normal path (foreign key from source to target).
             * E.g. Emp e -> Com c: from Emp e join Com t on e.company_id=t._id
             */
            Expression<?> on = new Equality(context,
                                            new ColumnRef(context,
                                                          sourceAlias,
                                                          reversePath ? link.targetColumns().get(0)
                                                                      : link.sourceColumns().get(0)),
                                            new ColumnRef(context,
                                                          alias,
                                                          reversePath ? link.sourceColumns().get(0)
                                                                      : link.targetColumns().get(0)));
            if (link.sourceColumns().size() > 1) {
              for (int j = 1; j < link.sourceColumns().size(); j++) {
                on = new LogicalAnd(context,
                                    on,
                                    new Equality(context,
                                                 new ColumnRef(context,
                                                               sourceAlias,
                                                               reversePath ? link.targetColumns().get(j)
                                                                           : link.sourceColumns().get(j)),
                                                 new ColumnRef(context,
                                                               alias,
                                                               reversePath ? link.sourceColumns().get(j)
                                                                           : link.targetColumns().get(j))));
              }
            }
            if (i == shortestPath.size() - 1
                && by.column != null
                && restriction.values != null
                && !restriction.values.isEmpty()) {
              /*
               * Add restrict clause on the last part of the path
               * (which is the target restricted table).
               */
              on = new LogicalAnd(context,
                                  on,
                                  restrictClause(context,
                                                 reversePath ? link.table() : link.targetTable(),
                                                 alias,
                                                 restriction.excluded,
                                                 by.column,
                                                 restriction.values));
            }
            pathJoin = new JoinTableExpr(context,
                                         pathJoin,
                                         null,
                                         new SingleTableExpr(context,
                                                             reversePath ? link.table() : link.targetTable(),
                                                             alias),
                                         on);
            sourceAlias = alias;
          }
          toReplace.replaceWith(pathJoin);
          List<Column> cols = columns();
          if (cols != null && cols.size() == 1) {
            Column singleCol = cols.get(0);
            if (String.valueOf(singleCol.expr()).equals("*")) {
              columns(shortestPathSource.alias() + ".*");
            }
          }
          type = null;
        } else {
          /*
           * The shortestPathSource is the table to restrict (there's no need for a path).
           */
          alias = shortestPathSource.alias();
          if (by.column != null
              && restriction.values != null
              && !restriction.values.isEmpty()) {
            Expression<?> restrict = restrictClause(context,
                                                    shortestPathSource.tableName(),
                                                    shortestPathSource.alias(),
                                                    restriction.excluded,
                                                    by.column,
                                                    restriction.values);
            where(where() == null
                  ? restrict
                  : new LogicalAnd(context, where(), restrict));
          }
        }
      }
      return T2.of(joined, alias);
    }
  }

  private static Expression<?> restrictClause(Context context,
                                              String restrictTableName,
                                              String restrictTableAlias,
                                              boolean exclude,
                                              String restrictColumn,
                                              Set<String> restrictValues) {
    if (restrictColumn == null
        || restrictValues == null
        || restrictValues.isEmpty()) {
      var one = new IntegerLiteral(context, 1L);
      return new Equality(context, one, one);
    } else {
      BaseRelation relation = context.structure.relation(restrictTableName);
      Column column = relation.column(restrictColumn);
      return new In(context,
                    new ColumnRef(context, restrictTableAlias, restrictColumn),
                    exclude,
                    restrictValues.stream()
                                  .map(v -> Literal.makeLiteral(context, v, column.type()))
                                  .collect(toList()),
                    null);
    }
  }

  /**
   * Result type of the query/update.
   */
  private volatile Selection type;

  private static final System.Logger log = System.getLogger(QueryUpdate.class.getName());
}