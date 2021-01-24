/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.base.string.Strings;
import ma.vi.esql.function.Function;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Macro;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.define.GroupBy;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.expression.ColumnRef;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.parser.expression.IntegerLiteral;
import ma.vi.esql.type.AliasedRelation;
import ma.vi.esql.type.BaseRelation;
import ma.vi.esql.type.Relation;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.joining;
import static ma.vi.base.tuple.T2.of;
import static ma.vi.esql.parser.Translatable.Target.ESQL;
import static ma.vi.esql.parser.Translatable.Target.HSQLDB;

/**
 * An ESQL select statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Select extends QueryUpdate implements Macro {
  public Select(Context             context,
                Metadata            metadata,
                boolean             distinct,
                List<Expression<?>> distinctOn,
                boolean             explicit,
                List<Column>        columns,
                TableExpr           from,
                Expression<?>       where,
                GroupBy             groupBy,
                Expression<?>       having,
                List<Order>         orderBy,
                Expression<?>       offset,
                Expression<?>       limit) {
    super(context, "Select",
        of("distinct",    new Esql<>(context, distinct)),
        of("distinctOn",  new Esql<>(context, "distinctOn", distinctOn)),
        of("explicit",    new Esql<>(context, explicit)),
        of("metadata",    metadata),
        of("columns",     new Esql<>(context, columns)),
        of("tables",      from),
        of("where",       where),
        of("groupBy",     groupBy),
        of("having",      having),
        of("orderBy",     new Esql<>(context, "orderBy", orderBy)),
        of("offset",      offset),
        of("limit",       limit));

    /*
     * Rename column names to be unique without random characters.
     */
    Set<String> names = new HashSet<>();
    for (Column column: columns) {
      String alias = column.alias();
      if (alias.startsWith("__auto_col")) {
        if (column.expr() instanceof FunctionCall) {
          alias = makeUnique(names, ((FunctionCall)column.expr()).functionName());
        } else {
          alias = makeUnique(names, "column");
        }
      } else {
        alias = makeUnique(names, alias);
      }
      column.alias(alias);
    }
  }

  public Select(Select other) {
    super(other);
  }

  @Override
  public Select copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Select(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public boolean modifying() {
    return false;
  }

  private static String makeUnique(Set<String> names, String alias) {
    int unique = 1;
    String name = alias;
    while (names.contains(name)) {
      name = alias + "_" + (unique++);
    }
    names.add(name);
    return name;
  }

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
    Map<String, String> aliased = new HashMap<>();
    Map<String, Column> resolvedColumns = new LinkedHashMap<>();
    for (Column column: columns()) {
      if (column instanceof StarColumn) {
        expanded = true;
        StarColumn all = (StarColumn)column;
        String qualifier = all.qualifier();
        Relation rel = qualifier == null ? fromType : fromType.forAlias(qualifier);
        for (Column relCol: rel.columns()) {
          String alias = relCol.alias();
          if (alias == null) {
            alias = Strings.makeUnique(resolvedColumns.keySet(), "col", false);
          }
          Column col;
          if (rel instanceof BaseRelation
           || (rel instanceof AliasedRelation && ((AliasedRelation)rel).relation instanceof BaseRelation)) {
            col = relCol.copy();
            if (qualifier != null) {
              ColumnRef.qualify(col.expr(), qualifier, null, true);
            }
          } else {
            col = new Column(context,
                             alias,
                             new ColumnRef(context, qualifier, relCol.alias()),
                             null);
          }

          int pos = alias.indexOf('/');
          if (pos == -1) {
            /*
             * Normal column (not metadata).
             */
            if (resolvedColumns.containsKey(alias)) {
              alias = Strings.makeUnique(resolvedColumns.keySet(), alias, false);
            }
            if (relCol.alias() != null && !alias.equals(relCol.alias())) {
              aliased.put(relCol.alias(), alias);
              col.alias(alias);
            }
            resolvedColumns.put(alias, col);

          } else if (pos > 0) {
            /*
             * Column metadata
             */
            String columnName = alias.substring(0, pos);
            if (aliased.containsKey(columnName)) {
              // replace column name with replacement if the column name was changed
              String aliasName = aliased.get(columnName);
              alias = aliasName + alias.substring(pos);
              col.alias(alias);
            }
            resolvedColumns.put(alias, col);

          } else if (!resolvedColumns.containsKey(alias)) {
            /*
             * table metadata first encounter (by elimination, pos==0 in this case)
             */
            resolvedColumns.put(alias, col);
          }
        }
      } else {
        resolvedColumns.put(column.alias(), column);
      }
    }
    columns(new ArrayList<>(resolvedColumns.values()));
    return expanded;
  }

  @Override
  public QueryTranslation translate(Target target, Map<String, Object> parameters) {
    boolean addAttributes = (Boolean)parameters.getOrDefault("addAttributes", true);
    boolean optimiseAttributesLoading = (Boolean)parameters.getOrDefault("optimiseAttributesLoading", true);
    if (target == Target.SQLSERVER) {
      List<Expression<?>> distinctOn = distinctOn();
      if (distinct() && distinctOn != null && !distinctOn.isEmpty()) {
        String subquery = "q_" + Strings.random();
        String rank = "r_" + Strings.random();
        StringBuilder st = new StringBuilder("select ");

        // add output clause
        StringBuilder columns = new StringBuilder();
        QueryTranslation q = constructResult(columns, target, null,
                                             addAttributes, optimiseAttributesLoading);

        st.append(columns);
        st.append(", ").append("row_number() over (partition by ");
        String partition = distinctOn.stream().map(e -> e.translate(target, parameters)).collect(joining(", "));
        st.append(partition);
        st.append(" order by ");
        if (orderBy() != null && !orderBy().isEmpty()) {
          st.append(orderBy().stream()
                             .map(e -> e.translate(target, parameters))
                             .collect(joining(", ")));
        } else {
          st.append(partition);
        }
        st.append(") ").append(rank);

        if (tables() != null) {
          st.append(" from ").append(tables().translate(target, parameters));
        }
        if (where() != null) {
          st.append(" where ").append(where().translate(target, parameters));
        }
        if (groupBy() != null) {
          st.append(groupBy().translate(target, parameters));
        }
        if (having() != null) {
          st.append(" having ").append(having().translate(target, parameters));
        }
//                if (orderBy() != null && !orderBy().isEmpty()) {
//                    st.append(" order by ")
//                      .append(orderBy().stream()
//                                       .map(e -> e.translate(target))
//                                       .collect(joining(", ")));
//                }
        if (offset() != null) {
          st.append(" offset ").append(offset().translate(target, parameters)).append(" rows");
        }
        if (limit() != null) {
          if (offset() == null) {
            // offset clause is mandatory with SQL Server when limit is specified
            st.append(" offset 0 rows");
          }
          st.append(" fetch next ").append(limit().translate(target, parameters)).append(" rows only");
        }
        String query = "select " + subquery + ".*"
            + "  from (" + st.toString() + ") " + subquery
            + " where " + subquery + "." + rank + "=1";
//                if (offset() == null && limit() == null && orderBy() != null && !orderBy().isEmpty()) {
//                    query += " order by "
//                           + orderBy().stream()
//                                      .map(e -> e.translate(target))
//                                      .collect(joining(", "));
//                }
        return new QueryTranslation(query,
                                    q.columns,
                                    q.columnToIndex,
                                    q.resultAttributeIndices,
                                    q.resultAttributes);
      } else {
        boolean hasComplexGroups = false;
        if (groupBy() != null) {
          for (Expression<?> expr: groupBy().groupBy()) {
            if (expr.firstChild(Select.class) != null) {
              hasComplexGroups = true;
              break;
            }
          }
        }
        if (hasComplexGroups) {
          /*
           * SQL Server does not support the use of sub-queries in group by expression.
           * For instance,
           *      select (select x from B) b, sum(a = 'X' -> amount : prev_amount) s from A
           *      group by (select x from B)
           *      having sum(a = 'Y' -> amount : prev_amount) > 100
           *
           *  is invalid in SQL Server.
           *
           * This can be rewritten as follows:
           *      select t0.b, sum(t0.a = 'X' -> t0.amount : t0.prev_amount) s from (
           *          select (select x from B) b, a, amount, prev_amount from A
           *      )  t0
           *      group by t0.b
           *      having sum(t0.a = 'Y' -> t0.amount : t0.prev_amount) > 100
           */

          /*
           * Create the columns for the inner and outer queries
           */
          String innerSelectAlias = "t" + Strings.random(4);
          List<Column> innerCols = new ArrayList<>();
          List<Column> outerCols = new ArrayList<>();
          List<Expression<?>> outerGroups = new ArrayList<>();
          Map<String, String> addedInnerCols = new HashMap<>();
          Set<Expression<?>> groups = new HashSet<>(groupBy().groupBy());
          for (Column column: columns()) {
            Expression<?> colExpr = column.expr();
            AtomicBoolean aggregate = new AtomicBoolean();
            colExpr.forEach(e -> {
              if (e instanceof FunctionCall) {
                Function function = context.structure.function(((FunctionCall)e).functionName());
                if (function != null && function.aggregate) {
                  aggregate.set(true);
                  return false;
                }
              }
              return true;
            });
            if (aggregate.get()) {
              /*
               * For an aggregate add columns that it refers to
               * to the inner query.
               */
              Expression<?> remapped = remapExpression(colExpr, addedInnerCols, innerCols, innerSelectAlias);
              outerCols.add(new Column(
                  context,
                  column.alias(),
                  remapped,
                  column.metadata()
              ));
            } else {
              String alias = column.alias();
              if (alias == null) {
                if (colExpr instanceof ColumnRef) {
                  alias = ((ColumnRef)colExpr).name();
                } else {
                  alias = "column";
                }
              }
              alias = Strings.makeUnique(new HashSet<>(addedInnerCols.values()), alias);
              innerCols.add(new Column(
                  context,
                  alias,
                  colExpr,
                  column.metadata()
              ));
              ColumnRef outerCol = new ColumnRef(context, innerSelectAlias, alias);
              outerCols.add(new Column(
                  context,
                  column.alias(),
                  outerCol,
                  column.metadata()
              ));
              if (groups.contains(colExpr)) {
                outerGroups.add(outerCol);
              }
            }
          }

          Expression<?> outerHaving = null;
          Expression<?> having = having();
          if (having != null) {
            /*
             * Add the aggregate to the outer query.
             */
            outerHaving = remapExpression(having,
                addedInnerCols,
                innerCols,
                innerSelectAlias);
          }

          Select innerSelect = new Select(
              context,
              metadata(),
              distinct(),
              distinctOn(),
              explicit(),
              innerCols,
              from(),
              where(),
              null,
              null,
              orderBy(),
              offset() == null ? new IntegerLiteral(context, 0L) : offset(),
              limit()
          );
          Select outerSelect = new Select(
              context,
              metadata(),
              distinct(),
              distinctOn(),
              explicit(),
              outerCols,
              new SelectTableExpr(context, innerSelect, innerSelectAlias),
              null,
              new GroupBy(context, outerGroups, groupBy().groupType()),
              outerHaving,
              null,
              null,
              null
          );
          return outerSelect.translate(target, parameters);

        } else {
          StringBuilder st = new StringBuilder("select ");
          if (distinct()) {
            st.append("distinct ");
          }
          // add output clause
          QueryTranslation q = constructResult(st, target, null,
                                               addAttributes, optimiseAttributesLoading);
          if (tables() != null) {
            st.append(" from ").append(tables().translate(target, parameters));
          }
          if (where() != null) {
            st.append(" where ").append(where().translate(target, parameters));
          }
          if (groupBy() != null) {
            st.append(groupBy().translate(target, parameters));
          }
          if (having() != null) {
            st.append(" having ").append(having().translate(target, parameters));
          }
          if (orderBy() != null && !orderBy().isEmpty()) {
            st.append(" order by ")
              .append(orderBy().stream()
                               .map(e -> e.translate(target, parameters))
                               .collect(joining(", ")));
          }
          if (offset() != null) {
            st.append(" offset ").append(offset().translate(target, parameters)).append(" rows");
          }
          if (limit() != null) {
            if (offset() == null) {
              // offset clause is mandatory with SQL Server when limit is specified
              st.append(" offset 0 rows");
            }
            st.append(" fetch next ").append(limit().translate(target, parameters)).append(" rows only");
          }
          return new QueryTranslation(st.toString(),
                                      q.columns,
                                      q.columnToIndex,
                                      q.resultAttributeIndices,
                                      q.resultAttributes);
        }
      }
    } else if (target == ESQL) {
      StringBuilder st = new StringBuilder("select ");
      if (distinct()) {
        st.append("distinct ");
        List<Expression<?>> distinctOn = distinctOn();
        if (distinctOn != null && !distinctOn.isEmpty()) {
          st.append("on (")
            .append(distinctOn.stream().map(e -> e.translate(target, parameters)).collect(joining(", ")))
            .append(") ");
        }
      }

      if (explicit() != null && explicit()) {
        st.append("explicit ");
      }

      st.append(columns().stream()
                         .map(c -> c.translate(ESQL, parameters))
                         .collect(joining(", ")));

      if (tables() != null) {
        st.append(" from ").append(tables().translate(target, parameters));
      }
      if (where() != null) {
        st.append(" where ").append(where().translate(target, parameters));
      }
      if (groupBy() != null) {
        st.append(groupBy().translate(target, parameters));
      }
      if (having() != null) {
        st.append(" having ").append(having().translate(target, parameters));
      }
      if (orderBy() != null && !orderBy().isEmpty()) {
        st.append(" order by ")
          .append(orderBy().stream()
                           .map(e -> e.translate(target, parameters))
                           .collect(joining(", ")));
      }
      if (offset() != null) {
        st.append(" offset ").append(offset().translate(target, parameters));
      }
      if (limit() != null) {
        st.append(" limit ").append(limit().translate(target, parameters));
      }
      return new QueryTranslation(st.toString(), null, null, null, null);

    } else {
      StringBuilder st = new StringBuilder("select ");
      if (distinct()) {
        st.append("distinct ");
        List<Expression<?>> distinctOn = distinctOn();
        if (distinctOn != null && !distinctOn.isEmpty()) {
          st.append(target != HSQLDB ? "on (" : "(")
            .append(distinctOn.stream().map(e -> e.translate(target, parameters)).collect(joining(", ")))
            .append(") ");
        }
      }

      // add output clause
      QueryTranslation q = constructResult(st, target, null,
                                           addAttributes, optimiseAttributesLoading);
      if (tables() != null) {
        st.append(" from ").append(tables().translate(target, parameters));
      }
      if (where() != null) {
        st.append(" where ").append(where().translate(target, parameters));
      }
      if (groupBy() != null) {
        st.append(groupBy().translate(target, parameters));
      }
      if (having() != null) {
        st.append(" having ").append(having().translate(target, parameters));
      }
      if (orderBy() != null && !orderBy().isEmpty()) {
        st.append(" order by ")
          .append(orderBy().stream()
                           .map(e -> e.translate(target, parameters))
                           .collect(joining(", ")));
      }
      if (offset() != null) {
        st.append(" offset ").append(offset().translate(target, parameters));
      }
      if (limit() != null) {
        st.append(" limit ").append(limit().translate(target, parameters));
      }
      return new QueryTranslation(st.toString(),
                                  q.columns,
                                  q.columnToIndex,
                                  q.resultAttributeIndices,
                                  q.resultAttributes);
    }
  }

  @Override protected boolean grouped() {
    if (groupBy() != null) {
      return true;
    } else if (columns().size() == 1) {
      /*
       * A select is also grouped if its single column is an aggregate function
       * such as count or max.
       */
      Column col = columns().get(0);
      Expression<?> expr = col.expr();
      if (expr instanceof FunctionCall) {
        FunctionCall fc = (FunctionCall)expr;
        Function function = context.structure.function(fc.functionName());
        return function != null && function.aggregate;
      }
    }
    return false;
  }

  /**
   * For SQL Server complex groups support:
   * <ol>
   *     <li>Find column references in the expression and add to the columns
   *         of the inner query.</li>
   *     <li>Create a column referencing the inner columns to be added to
   *         the outer query.</li>
   * </ol>
   */
  public Expression<?> remapExpression(Expression<?> expression,
                                       Map<String, String> addedInnerCols,
                                       List<Column> innerCols,
                                       String innerSelectAlias) {
    /*
     * Find column references in the expression and add to the columns
     * of the inner query.
     */
    expression.forEach(e -> {
      if (e instanceof ColumnRef) {
        ColumnRef ref = (ColumnRef)e;
        if (!addedInnerCols.containsKey(ref.qualifiedName())) {
          String alias = Strings.makeUnique(new HashSet<>(addedInnerCols.values()), ref.name());
          innerCols.add(new Column(
              context, alias, ref.copy(), null
          ));
          addedInnerCols.put(ref.qualifiedName(), alias);
        }
      }
      return true;
    });

    /*
     * Remap expression to be added to outer query.
     */
    return (Expression<?>)expression.map(e -> {
      if (e instanceof ColumnRef) {
        ColumnRef ref = (ColumnRef)e;
        String qualifiedName = ref.qualifiedName();
        ref.qualifier(innerSelectAlias);
        if (addedInnerCols.containsKey(qualifiedName)) {
          ref.name(addedInnerCols.get(qualifiedName));
        }
      }
      return e;
    });
  }

  public Boolean distinct() {
    return childValue("distinct");
  }

  public Select distinct(Boolean distinct) {
    childValue("distinct", distinct);
    return this;
  }

  public List<Expression<?>> distinctOn() {
    return child("distinctOn").childrenList();
  }

  public Select distinctOn(List<Expression<?>> on) {
    childrenList("distinctOn", on);
    return this;
  }

  public Boolean explicit() {
    return childValue("explicit");
  }

  public Select explicit(Boolean explicit) {
    childValue("explicit", explicit);
    return this;
  }

  public GroupBy groupBy() {
    return child("groupBy");
  }

  public Select groupBy(GroupBy groupBy) {
    child("groupBy", groupBy);
    return this;
  }

  public Expression<?> having() {
    return child("having");
  }

  public Select having(Expression<?> having) {
    child("having", having);
    return this;
  }

  public List<Order> orderBy() {
    return child("orderBy").childrenList();
  }

  public Select orderBy(List<Order> orderBy) {
    childrenList("orderBy", orderBy);
    return this;
  }

  public Expression<?> offset() {
    return child("offset");
  }

  public Select offset(Expression<?> offset) {
    child("offset", offset);
    return this;
  }

  public Expression<?> limit() {
    return child("limit");
  }

  public Select limit(Expression<?> limit) {
    child("limit", limit);
    return this;
  }
}
