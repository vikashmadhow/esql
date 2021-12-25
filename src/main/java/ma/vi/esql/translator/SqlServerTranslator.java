package ma.vi.esql.translator;

import ma.vi.base.string.Strings;
import ma.vi.esql.function.Function;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.TranslationException;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.syntax.expression.literal.IntegerLiteral;
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.syntax.modify.InsertRow;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.query.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.min;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SqlServerTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.SQLSERVER;
  }

  @Override
  protected QueryTranslation translate(Select select, EsqlPath path, Map<String, Object> parameters) {
    /*
     * If group by expression list contains numbers, those refer to the column expressions
     * in the expressions list. This is not supported in SQL server, so replace the
     * column reference with the actual column expression.
     */
    boolean hasComplexGroups = false;
    GroupBy groupBy = select.groupBy();
    if (groupBy != null) {
      List<Expression<?, String>> newGroupExpressions = new ArrayList<>();
      boolean groupExpressionsChanged = false;
      for (Expression<?, String> expr: groupBy.groupBy()) {
        if (expr.descendent(Select.class) != null) {
          hasComplexGroups = true;
        }
        if (expr instanceof IntegerLiteral){
          groupExpressionsChanged = true;
          int colPos = (((IntegerLiteral)expr).value).intValue();
          if (select.columns().size() < colPos) {
            throw new TranslationException("Group " + colPos + " does not exist in the columns list of the query");
          } else {
            newGroupExpressions.add(select.columns().get(colPos - 1).expression().copy());
          }
        } else {
          newGroupExpressions.add(expr.copy());
        }
      }
      if (groupExpressionsChanged) {
        select = select.groupBy(new GroupBy(select.context, newGroupExpressions, groupBy.groupType()));
      }
    }

    List<Expression<?, String>> distinctOn = select.distinctOn();
    boolean subSelect = path.ancestor("tables") != null || path.ancestor(Cte.class) != null;
    if (select.distinct() && distinctOn != null && !distinctOn.isEmpty()) {
      String subquery = "q_" + Strings.random();
      String rank = "r_" + Strings.random();
      StringBuilder st = new StringBuilder("select ");

      // add output clause
      StringBuilder columns = new StringBuilder();
      QueryTranslation q = select.constructResult(columns, target(), path, null, parameters);
      parameters.put("addIif", false);

      st.append(columns);
      st.append(", ").append("row_number() over (partition by ");
      String partition = distinctOn.stream()
                                   .map(e -> e.translate(target(), path.add(e), parameters))
                                   .collect(joining(", "));
      st.append(partition);
      st.append(" order by ");
      if (select.orderBy() != null && !select.orderBy().isEmpty()) {
        st.append(select.orderBy().stream()
                        .map(e -> e.translate(target(), path.add(e), parameters))
                        .collect(joining(", ")));
      } else {
        st.append(partition);
      }
      st.append(") ").append(rank);

      if (select.tables() != null) {
        st.append(" from ").append(select.tables().translate(target(), path.add(select.tables()), parameters));
      }
      if (select.where() != null) {
        st.append(" where ").append(select.where().translate(target(), path.add(select.where()), parameters));
      }
      if (select.groupBy() != null) {
        st.append(select.groupBy().translate(target(), path.add(select.groupBy()), parameters));
      }
      if (select.having() != null) {
        st.append(" having ").append(select.having().translate(target(), path.add(select.having()), parameters));
      }
      if (select.orderBy() != null && !select.orderBy().isEmpty()) {
        st.append(" order by ")
          .append(select.orderBy().stream()
                        .map(e -> e.translate(target(), path.add(e)))
                        .collect(joining(", ")));
        if (select.offset() == null && select.limit() == null) {
          /*
           * Offset is required when order is specified in an inner query in SQL Server
           */
          st.append(" offset 0 rows");
        }
      }
      if (select.offset() != null) {
        st.append(" offset ")
          .append(select.offset().translate(target(), path.add(select.offset()), parameters))
          .append(" rows");
      }
      if (select.limit() != null) {
        if (select.offset() == null) {
          // offset clause is mandatory with SQL Server when limit is specified
          st.append(" offset 0 rows");
        }
        st.append(" fetch next ")
          .append(select.limit().translate(target(), path.add(select.limit()), parameters))
          .append(" rows only");
      }
      String query = "select " + subquery + ".*"
          + "  from (" + st.toString() + ") " + subquery
          + " where " + subquery + "." + rank + "=1";
//      if (offset() == null && limit() == null && orderBy() != null && !orderBy().isEmpty()) {
//        query += " order by "
//            + orderBy().stream()
//                       .map(e -> e.translate(target))
//                       .collect(joining(", "));
//      }
      return new QueryTranslation(query,
                                  q.columns(),
                                  q.columnToIndex(),
                                  q.resultAttributeIndices(),
                                  q.resultAttributes());
    } else {
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
        List<Expression<?, String>> outerGroups = new ArrayList<>();
        Map<String, String> addedInnerCols = new HashMap<>();
        Set<Expression<?, String>> groups = new HashSet<>(select.groupBy().groupBy());
        for (Column column: select.columns()) {
          Expression<?, String> colExpr = column.expression();
          AtomicBoolean aggregate = new AtomicBoolean();
          Context context = select.context;
          colExpr.forEach((e, p) -> {
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
            Expression<?, String> remapped = select.remapExpression(colExpr, addedInnerCols, innerCols, innerSelectAlias);
            outerCols.add(new Column(
                select.context,
                column.name(),
                remapped,
                column.metadata()
            ));
          } else {
            String alias = column.name();
            if (alias == null) {
              if (colExpr instanceof ColumnRef) {
                alias = ((ColumnRef)colExpr).name();
              } else {
                alias = "column";
              }
            }
            alias = Strings.makeUnique(new HashSet<>(addedInnerCols.values()), alias);
            innerCols.add(new Column(
                select.context,
                alias,
                colExpr,
                column.metadata()
            ));
            ColumnRef outerCol = new ColumnRef(select.context, innerSelectAlias, alias);
            outerCols.add(new Column(
                select.context,
                column.name(),
                outerCol,
                column.metadata()
            ));
            if (groups.contains(colExpr)) {
              outerGroups.add(outerCol);
            }
          }
        }

        Expression<?, String> outerHaving = null;
        Expression<?, String> having = select.having();
        if (having != null) {
          /*
           * Add the aggregate to the outer query.
           */
          outerHaving = select.remapExpression(having,
                                        addedInnerCols,
                                        innerCols,
                                        innerSelectAlias);
        }

        Select innerSelect = new Select(
            select.context,
            select.metadata(),
            select.distinct(),
            select.distinctOn(),
            select.explicit(),
            innerCols,
            select.from(),
            select.where(),
            null,
            null,
            select.orderBy(),
            select.offset() == null ? new IntegerLiteral(select.context, 0L) : select.offset(),
            select.limit()
        );
        Select outerSelect = new Select(
            select.context,
            select.metadata(),
            select.distinct(),
            select.distinctOn(),
            select.explicit(),
            outerCols,
            new SelectTableExpr(select.context, innerSelect, innerSelectAlias),
            null,
            new GroupBy(select.context, outerGroups, select.groupBy().groupType()),
            outerHaving,
            null,
            null,
            null
        );
        return outerSelect.translate(target(), path.add(outerSelect), parameters);

      } else {
        StringBuilder st = new StringBuilder("select ");
        if (select.distinct()) {
          st.append("distinct ");
        }
        // add output clause
        QueryTranslation q = select.constructResult(st, target(), path, null, parameters);
        parameters.put("addIif", false);

        if (select.tables() != null) {
          st.append(" from ").append(select.tables().translate(target(), path.add(select.tables()), parameters));
        }
        if (select.where() != null) {
          st.append(" where ").append(select.where().translate(target(), path.add(select.where()), parameters));
        }
        if (select.groupBy() != null) {
          st.append(select.groupBy().translate(target(), path.add(select.groupBy()), parameters));
        }
        if (select.having() != null) {
          st.append(" having ").append(select.having().translate(target(), path.add(select.having()), parameters));
        }
        if (select.orderBy() != null && !select.orderBy().isEmpty()) {
          st.append(" order by ")
            .append(select.orderBy().stream()
                          .map(e -> e.translate(target(), path.add(e), parameters))
                          .collect(joining(", ")));
          if (subSelect && select.offset() == null && select.limit() == null) {
            /*
             * Offset is required when order is specified in an inner query in SQL Server
             */
            st.append(" offset 0 rows");
          }
        }
        if (select.offset() != null) {
          st.append(" offset ").append(select.offset().translate(target(), path.add(select.offset()), parameters)).append(" rows");
        }
        if (select.limit() != null) {
          if (select.offset() == null) {
            // offset clause is mandatory with SQL Server when limit is specified
            st.append(" offset 0 rows");
          }
          st.append(" fetch next ").append(select.limit().translate(target(), path.add(select.limit()), parameters)).append(" rows only");
        }
        return new QueryTranslation(st.toString(),
                                    q.columns(),
                                    q.columnToIndex(),
                                    q.resultAttributeIndices(),
                                    q.resultAttributes());
      }
    }
  }

  @Override
  protected QueryTranslation translate(Update update,
                                       EsqlPath path,
                                       Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("update ");

    TableExpr from = update.tables();
    st.append('"').append(update.updateTableAlias()).append('"');
    Update.addSet(st, update.set(), target(), true, path);

    // output clause for SQL Server if specified
    QueryTranslation q = null;
    if (update.columns() != null && !update.columns().isEmpty()) {
      st.append(" output ");
      q = update.constructResult(st, target(), path, "inserted", parameters);
      parameters.put("addIif", false);
    }
    st.append(" from ").append(from.translate(target(), path.add(from), parameters));

    if (update.where() != null) {
      st.append(" where ").append(update.where().translate(target(), path.add(update.where()), parameters));
    }
    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                  emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns(), q.columnToIndex(),
                                  q.resultAttributeIndices(), q.resultAttributes());
    }
  }

  @Override
  protected QueryTranslation translate(Delete delete, EsqlPath path, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("delete ").append((delete.deleteTableAlias()));
    QueryTranslation q = null;

    TableExpr from = delete.tables();
    if (delete.columns() != null && !delete.columns().isEmpty()) {
      st.append(" output ");
      q = delete.constructResult(st, target(), path, "deleted", parameters);
      parameters.put("addIif", false);
    }
    st.append(" from ").append(from.translate(target(), path.add(from), parameters));

    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target(), path.add(delete.where()), parameters));
    }
    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                  emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns(), q.columnToIndex(),
                                  q.resultAttributeIndices(), q.resultAttributes());
    }
  }

  @Override
  protected QueryTranslation translate(Insert insert, EsqlPath path, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("insert into ");
    TableExpr table = insert.tables();
    if (!(table instanceof SingleTableExpr)) {
      throw new TranslationException("Insert only works with single tables. A " + table.getClass().getSimpleName()
                                         + " was found instead.");
    }
    st.append(Type.dbTableName(((SingleTableExpr)table).tableName(), target()));

    List<String> fields = insert.fields();
    if (fields != null && !fields.isEmpty()) {
      st.append(fields.stream()
                      .map(f -> '"' + f + '"')
                      .collect(joining(", ", "(", ")")));
    }

    // output clause for SQL Server if specified
    QueryTranslation q = null;
    if (insert.columns() != null && !insert.columns().isEmpty()) {
      st.append(" output ");
      q = insert.constructResult(st, target(), path, "inserted", parameters);
      parameters.put("addIif", false);
    }

    List<InsertRow> rows = insert.rows();
    if (rows != null && !rows.isEmpty()) {
      st.append(rows.stream()
                    .map(row -> row.translate(target(), path.add(row), parameters))
                    .collect(joining(", ", " values", "")));

    } else if (insert.defaultValues()) {
      st.append(" default values");

    } else {
      Map<String, Object> params = new HashMap<>();
      params.put("addAttributes", false);
      st.append(' ').append(insert.select().translate(target(),
                                                      path.add(insert.select()),
                                                      params).statement());
    }

    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(), emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns(), q.columnToIndex(),
                                  q.resultAttributeIndices(), q.resultAttributes());
    }
  }

  /**
   * To simulate boolean values in SQL Server, we need to surround boolean-returning
   * expressions with IIF in Sql Server but only where boolean expressions are not
   * allowed. Latter are allowed only in where, on and having clause, which IIF is
   * required if the boolean expressions appears in a columns list, group-by list
   * or order-by list. Because ESQL statements can be nested we need to look at
   * the distance of ancestor to determine whether IIF is needed or not.
   */
  public static boolean requireIif(EsqlPath path, Map<String, Object> parameters) {
    if (parameters.containsKey("addIif")) {
       return (Boolean)parameters.get("addIif");
    } else {
      int columnsDist = path.ancestorDistance("columns");
      int orderByDist = path.ancestorDistance("orderBy");
      int groupByDist = path.ancestorDistance("groupBy");
      int whereDist = path.ancestorDistance("where");
      int havingDist = path.ancestorDistance("having");
      int onDist = path.ancestorDistance("on");

      int reqIf = min(columnsDist, min(orderByDist, groupByDist));
      int noIf = min(whereDist, min(havingDist, onDist));

      return reqIf < noIf;
    }
  }
}
