package ma.vi.esql.translator;

import ma.vi.base.string.Strings;
import ma.vi.esql.function.Function;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.define.GroupBy;
import ma.vi.esql.parser.expression.ColumnRef;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.parser.expression.IntegerLiteral;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
  protected QueryTranslation translate(Select select) {
    List<Expression<?>> distinctOn = select.distinctOn();
    if (select.distinct() && distinctOn != null && !distinctOn.isEmpty()) {
      String subquery = "q_" + Strings.random();
      String rank = "r_" + Strings.random();
      StringBuilder st = new StringBuilder("select ");

      // add output clause
      StringBuilder columns = new StringBuilder();
      QueryTranslation q = select.constructResult(columns, target(), null,
                                                  true, true);

      st.append(columns);
      st.append(", ").append("row_number() over (partition by ");
      String partition = distinctOn.stream()
                                   .map(e -> e.translate(target()))
                                   .collect(joining(", "));
      st.append(partition);
      st.append(" order by ");
      if (select.orderBy() != null && !select.orderBy().isEmpty()) {
        st.append(select.orderBy().stream()
                        .map(e -> e.translate(target()))
                        .collect(joining(", ")));
      } else {
        st.append(partition);
      }
      st.append(") ").append(rank);

      if (select.tables() != null) {
        st.append(" from ").append(select.tables().translate(target()));
      }
      if (select.where() != null) {
        st.append(" where ").append(select.where().translate(target()));
      }
      if (select.groupBy() != null) {
        st.append(select.groupBy().translate(target()));
      }
      if (select.having() != null) {
        st.append(" having ").append(select.having().translate(target()));
      }
//      if (orderBy() != null && !orderBy().isEmpty()) {
//        st.append(" order by ")
//          .append(orderBy().stream()
//                           .map(e -> e.translate(target))
//                           .collect(joining(", ")));
//      }
      if (select.offset() != null) {
        st.append(" offset ")
          .append(select.offset().translate(target()))
          .append(" rows");
      }
      if (select.limit() != null) {
        if (select.offset() == null) {
          // offset clause is mandatory with SQL Server when limit is specified
          st.append(" offset 0 rows");
        }
        st.append(" fetch next ")
          .append(select.limit().translate(target()))
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
                                  q.columns,
                                  q.columnToIndex,
                                  q.resultAttributeIndices,
                                  q.resultAttributes);
    } else {
      boolean hasComplexGroups = false;
      if (select.groupBy() != null) {
        for (Expression<?> expr: select.groupBy().groupBy()) {
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
        Set<Expression<?>> groups = new HashSet<>(select.groupBy().groupBy());
        for (Column column: select.columns()) {
          Expression<?> colExpr = column.expr();
          AtomicBoolean aggregate = new AtomicBoolean();
          colExpr.forEach(e -> {
            if (e instanceof FunctionCall) {
              Function function = select.context.structure.function(((FunctionCall)e).functionName());
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
            Expression<?> remapped = select.remapExpression(colExpr, addedInnerCols, innerCols, innerSelectAlias);
            outerCols.add(new Column(
                select.context,
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
                select.context,
                alias,
                colExpr,
                column.metadata()
            ));
            ColumnRef outerCol = new ColumnRef(select.context, innerSelectAlias, alias);
            outerCols.add(new Column(
                select.context,
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
        Expression<?> having = select.having();
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
        return outerSelect.translate(target(), true, true);

      } else {
        StringBuilder st = new StringBuilder("select ");
        if (select.distinct()) {
          st.append("distinct ");
        }
        // add output clause
        QueryTranslation q = select.constructResult(st, target(), null,
                                                    true, true);
        if (select.tables() != null) {
          st.append(" from ").append(select.tables().translate(target()));
        }
        if (select.where() != null) {
          st.append(" where ").append(select.where().translate(target()));
        }
        if (select.groupBy() != null) {
          st.append(select.groupBy().translate(target()));
        }
        if (select.having() != null) {
          st.append(" having ").append(select.having().translate(target()));
        }
        if (select.orderBy() != null && !select.orderBy().isEmpty()) {
          st.append(" order by ")
            .append(select.orderBy().stream()
                             .map(e -> e.translate(target()))
                             .collect(joining(", ")));
        }
        if (select.offset() != null) {
          st.append(" offset ").append(select.offset().translate(target())).append(" rows");
        }
        if (select.limit() != null) {
          if (select.offset() == null) {
            // offset clause is mandatory with SQL Server when limit is specified
            st.append(" offset 0 rows");
          }
          st.append(" fetch next ").append(select.limit().translate(target())).append(" rows only");
        }
        return new QueryTranslation(st.toString(),
                                    q.columns,
                                    q.columnToIndex,
                                    q.resultAttributeIndices,
                                    q.resultAttributes);
      }
    }
  }

  @Override
  protected QueryTranslation translate(Update update) {
    StringBuilder st = new StringBuilder("update ");

    TableExpr from = update.tables();
    st.append('"').append(update.updateTableAlias()).append('"');
    Update.addSet(st, update.set(), target(), true);

    // output clause for SQL Server if specified
    QueryTranslation q = null;
    if (update.columns() != null && !update.columns().isEmpty()) {
      st.append(" output ");
      q = update.constructResult(st, target(), "inserted", true, true);
    }
    st.append(" from ").append(from.translate(target()));

    if (update.where() != null) {
      st.append(" where ").append(update.where().translate(target()));
    }
    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                  emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                  q.resultAttributeIndices, q.resultAttributes);
    }
  }

  @Override
  protected QueryTranslation translate(Delete delete) {
    StringBuilder st = new StringBuilder("delete ").append((delete.deleteTableAlias()));
    QueryTranslation q = null;

    TableExpr from = delete.tables();
    if (delete.columns() != null && !delete.columns().isEmpty()) {
      st.append(" output ");
      q = delete.constructResult(st, target(), "deleted", true, true);
    }
    st.append(" from ").append(from.translate(target()));

    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target()));
    }
    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                  emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                  q.resultAttributeIndices, q.resultAttributes);
    }
  }
}