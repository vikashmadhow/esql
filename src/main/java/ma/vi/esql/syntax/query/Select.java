/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.Filter;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.GroupedExpression;
import ma.vi.esql.syntax.expression.logical.And;
import ma.vi.esql.syntax.expression.logical.Not;
import ma.vi.esql.syntax.expression.logical.Or;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static ma.vi.base.tuple.T2.of;
import static ma.vi.esql.exec.Filter.Op.AND;
import static ma.vi.esql.exec.Filter.Op.OR;

/**
 * An ESQL select statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Select extends QueryUpdate {
  @SafeVarargs
  public Select(Context                  context,
                Metadata                 metadata,
                boolean                  distinct,
                List<Expression<?, ?>>   distinctOn,
                boolean                  explicit,
                List<Column>             columns,
                TableExpr                from,
                Expression<?, String>    where,
                GroupBy                  groupBy,
                Expression<?, String>    having,
                List<Order>              orderBy,
                Expression<?, String>    offset,
                Expression<?, String>    limit,
                T2<String, ? extends Esql<?, ?>>... children) {
    super(context, "Select",
          Stream.concat(
            Stream.of(
              of("distinct",    new Esql<>(context, distinct)),
              of("explicit",    new Esql<>(context, explicit)),
              of("tables",      from),
              of("metadata",    metadata),
              of("distinctOn",  new Esql<>(context, "distinctOn", distinctOn)),
              of("columns",     new ColumnList(context, columns)),
              of("where",       where),
              of("groupBy",     groupBy),
              of("having",      having),
              of("orderBy",     orderBy == null ? null : new Esql<>(context, "orderBy", orderBy)),
              of("offset",      offset),
              of("limit",       limit)),
            Stream.of(children)).toArray(T2[]::new));
  }

  public Select(Select other) {
    super(other);
  }

  @SafeVarargs
  public Select(Select other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Select copy() {
    return new Select(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Select copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Select(this, value, children);
  }

  @Override
  public boolean modifying() {
    return false;
  }

  @Override protected boolean grouped() {
    if (groupBy() != null) {
      return true;
    } else if (columns().size() == 1) {
      /*
       * A 'select' is also grouped if its single column is an aggregate function
       * such as count or max.
       */
      Column col = columns().get(0);
      Expression<?, ?> expr = col.expression();
      if (expr instanceof FunctionCall fc) {
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
  public Expression<?, String> remapExpression(Expression<?, ?>     expression,
                                               Map<String, String>  addedInnerCols,
                                               List<Column>         innerCols,
                                               String               innerSelectAlias) {
    /*
     * Find column references in the expression and add to the columns
     * of the inner query.
     */
    expression.forEach((e, p) -> {
      if (e instanceof ColumnRef ref) {
        if (!addedInnerCols.containsKey(ref.qualifiedName())) {
          String alias = Strings.makeUnique(new HashSet<>(addedInnerCols.values()), ref.columnName());
          innerCols.add(new Column(context, alias, ref.copy(), ref.type(), null));
          addedInnerCols.put(ref.qualifiedName(), alias);
        }
      }
      return true;
    });

    /*
     * Remap expression to be added to outer query.
     */
    return (Expression<?, String>)expression.map((e, p) -> {
      if (e instanceof ColumnRef ref) {
        String qualifiedName = ref.qualifiedName();
        ref = ref.qualifier(innerSelectAlias);
        return addedInnerCols.containsKey(qualifiedName)
                  ? ref.columnName(addedInnerCols.get(qualifiedName))
                  : ref;
      }
      return e;
    });
  }

  @Override
  public Select filter(Filter filter) {
    FilterResult result = filter(tables(), where(), filter);
    if (result != null) {
      return new Select(context,
                        metadata(),
                        result.hasReverseLinks || distinct(),
                        distinctOn(),
                        explicit(),
                        columns(),
                        result.tables,
                        result.where,
                        groupBy(),
                        having(),
                        orderBy(),
                        offset(),
                        limit());
    } else {
      return this;
    }
  }

  public record FilterResult(TableExpr             tables,
                             Expression<?, String> where,
                             boolean               hasReverseLinks) {}

  public static FilterResult filter(TableExpr             tables,
                                    Expression<?, String> where,
                                    Filter                filter) {
    TableExpr.ShortestPath shortest = tables.findShortestPath(filter);
    if (shortest != null) {
      TableExpr.AppliedShortestPath applied = tables.applyShortestPath(shortest);
      if (filter.condition() != null) {
        /*
         * Add filter condition, changing its target table alias if needed.
         */
        Parser parser = new Parser(tables.context.structure);
        Expression<?, String> condition = parser.parseExpression(filter.condition());
        if (filter.alias() != null
        && !filter.alias().equals(applied.targetAlias())) {
          condition = (Expression<?, String>)condition.map((e, p) -> {
            if (e instanceof ColumnRef ref
             && filter.alias().equals(ref.qualifier())) {
              return new ColumnRef(e.context, applied.targetAlias(), ref.columnName(), ref.type());
            }
            return e;
          });
        }
        if (filter.exclude()) {
          condition = new Not(tables.context, condition);
        }
        where = where == null ? condition
                              : combine(where, condition, filter.op());
      }
      return new FilterResult(rotateLeft(applied.result()),
                              where,
                              shortest.path().hasReverseLink());
    } else {
      return null;
    }
  }

  /**
   * When applying the shortest path to create joins the resulting table expressions
   * may end up in the wrong orientation, which will cause translation to fail.
   * The correct orientation is a left-oriented tree, as such:
   * <pre>
   *            j
   *           / \
   *          j   d
   *         / \
   *        j   c
   *       / \
   *      a   b
   * </pre>
   *
   * However, after shortest path application, other tree forms, such as the
   * following one, might be returned:
   * <pre>
   *             j
   *           /   \
   *          j     j
   *         / \   / \
   *        a   b c   d
   * </pre>
   *
   * A left-form tree can be obtained by applying left-rotations to parts of the
   * tree which are in the right-orientation, which is performed by this method.
   */
  public static TableExpr rotateLeft(TableExpr tableExpr) {
    if (tableExpr instanceof AbstractJoinTableExpr j) {
      if (j.right() instanceof AbstractJoinTableExpr jright) {
        return jright.join(rotateLeft(j.join(j.left(), jright.left())),
                           rotateLeft(jright.right()));
      } else {
        return j.join(rotateLeft(j.left()), j.right());
      }
    } else {
      return tableExpr;
    }
  }

  /**
   * Combine expressions into a `where` condition using the filter operator provided.
   * The combination maintains the following constraints:
   * 1. Adding a new expression with an OR operator will add the result matching
   *    that expression to the result matching the condition without that expression;
   * 2. Adding a new expression with an AND operator will remove all result that
   *    does not simultaneously match the previous condition and the new expression.
   * In essence, adding a new expression should not result in changing the matching
   * set of condition in unpredictable ways (which can happen if groupings are
   * not applied correctly between combinations expressions with AND and OR).
   */
  public static Expression<?, String> combine(Expression<?, String> condition,
                                              Expression<?, String> expression,
                                              Filter.Op op) {
    if (condition instanceof And and) {
      if (op == AND) {
        return new And(condition.context, condition, expression);
      } else {
        return new Or(condition.context,
                      new GroupedExpression(condition.context, and),
                      expression);
      }
    } else if (condition instanceof Or or) {
      if (op == OR) {
        return new Or(condition.context, condition, expression);
      } else {
        return new And(condition.context,
                       new GroupedExpression(condition.context, or),
                       expression);
      }
    } else {
      return op == AND ? new And(condition.context, condition, expression)
                       : new  Or(condition.context, condition, expression);
    }
  }

  public Boolean distinct() {
    return childValue("distinct");
  }

  public List<Expression<?, ?>> distinctOn() {
    return child("distinctOn").children();
  }

  public Boolean explicit() {
    return childValue("explicit");
  }

  public GroupBy groupBy() {
    return child("groupBy");
  }

  public Select groupBy(GroupBy groupBy) {
    return set("groupBy", groupBy);
  }

  public Expression<?, String> having() {
    return child("having");
  }

  public List<Order> orderBy() {
    return child("orderBy") == null ? null : child("orderBy").children();
  }

  public Expression<?, String> offset() {
    return child("offset");
  }

  public Expression<?, String> limit() {
    return child("limit");
  }
}
