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
import ma.vi.esql.syntax.EsqlPath;
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
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

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
  public Select filter(Filter filter, boolean firstFilter, EsqlPath path) {
    if (getClass().equals(Select.class)) {
      FilterResult result = filter(tables(), filter, firstFilter, where());
      if (result != null) {
        /*
         * For recursive `WITH` query on SQL Server, `distinct` is not supported in
         * the first CTE. If this is the case, exclude putting distinct even when
         * there are reverse links on the path.
         */
        boolean doNotPutDistinct = false;
        if (context.structure.database.target() == SQLSERVER) {
          With with = path.ancestor(With.class);
          if (with != null && with.recursive()) {
            CompositeSelect sel = path.ancestor(CompositeSelect.class);
            if (sel == with.ctes().get(0).query()) {
              doNotPutDistinct = true;
            }
          }
        }
        return new Select(context,
                          metadata(),
                          (result.hasReverseLinks && !doNotPutDistinct) || distinct(),
                          distinctOn(),
                          explicit(),
                          columns(),
                          result.tables,
                          result.condition,
                          groupBy(),
                          having(),
                          orderBy(),
                          offset(),
                          limit());
      } else {
        return this;
      }
    } else {
      /*
       * For subclasses, assume that filtering is already taken care of by filtering
       * of their children (this is true for the current known subclasses which
       * include CompositeSelect and SelectExpression).
       */
      return this;
    }
  }

  public record FilterResult(TableExpr             tables,
                             Expression<?, String> condition,
                             boolean               hasReverseLinks) {}

  public static FilterResult filter(TableExpr             tables,
                                    Filter                filter,
                                    boolean               firstFilter,
                                    Expression<?, String> where) {
    while (filter.children().length == 1) {
      filter = filter.children()[0];
    }
    FilterResult result = filter.children().length == 0
                        ? singleFilter(tables, filter)
                        : multipleFilters(tables, filter.op(), filter.exclude(), filter.children());
    if (result != null
     && result.condition() != null
     && where != null) {
      where = combine(firstFilter ? new GroupedExpression(where.context, where)
                                  : where,
                      result.condition(),
                      firstFilter && filter.table() == null ? AND : filter.op()); // combine the 1st filter when it has children
                                                                                  // with the 'AND' connective as the filter.op
                                                                                  // is for connective to use for the children.
      result = new FilterResult(result.tables(), where, result.hasReverseLinks());
    }
    return result;
  }

  private static FilterResult singleFilter(TableExpr tables,
                                           Filter    filter) {
    TableExpr.ShortestPath shortest = tables.findShortestPath(filter);
    if (shortest != null) {
      TableExpr.AppliedShortestPath applied = tables.applyShortestPath(shortest, tables);
      Expression<?, String> condition = null;
      if (filter.condition() != null) {
        /*
         * Add filter condition, changing its target table alias if needed.
         */
        Parser parser = new Parser(tables.context.structure);
        condition = parser.parseExpression(filter.condition());
        if (!applied.targetAlias().equals(filter.alias())) {
          condition = (Expression<?, String>)condition.map((e, p) -> {
            if (e instanceof ColumnRef ref) {
              return new ColumnRef(e.context, applied.targetAlias(), ref.columnName(), ref.type());
            }
            return e;
          });
        }
        if (filter.exclude()) {
          condition = new Not(tables.context, condition);
        }
      }
      return new FilterResult(rotateLeft(applied.result()),
                              condition,
                              shortest.path().hasReverseLink());
    } else {
      return null;
    }
  }

  private static FilterResult multipleFilters(TableExpr  tables,
                                              Filter.Op  filterOp,
                                              boolean    exclude,
                                              Filter...  filters) {
    FilterResult result = null;
    for (Filter filter: filters) {
      while (filter.children().length == 1) {
        filter = filter.children()[0];
      }
      FilterResult subResult = filter.children().length == 0
                             ? singleFilter(tables, filter)
                             : multipleFilters(tables, filter.op(), filter.exclude(), filter.children());

      if (subResult != null) {
        Expression<?, String> subCondition = subResult.condition();
        if (result == null) {
          result = new FilterResult(subResult.tables(),
                                    subCondition,
                                    subResult.hasReverseLinks());
        } else {
          /*
           * Merge result
           */
          Expression<?, String> condition = result.condition();
          result = new FilterResult(subResult.tables(),
                                    condition    == null ? subCondition
                                  : subCondition == null ? null
                                  : filterOp     == AND  ? new And(condition.context, condition, subCondition)
                                                         : new Or (condition.context, condition, subCondition),
                                    result.hasReverseLinks() || subResult.hasReverseLinks());
        }
        tables = result.tables();
      }
    }
    if (result != null && result.condition() != null) {
      Expression<?, String> condition = result.condition();
      condition = new GroupedExpression(condition.context, condition);
      if (exclude) {
        condition = new Not(condition.context, condition);
      }
      result = new FilterResult(result.tables, condition, result.hasReverseLinks);
    }
    return result;
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
   * <ol>
   * <li>Adding a new expression with an OR operator will add the result matching
   *     that expression to the result matching the condition without that expression;</li>
   * <li>Adding a new expression with an AND operator will remove all result that
   *     does not simultaneously match the previous condition and the new expression.</li>
   * </ol>
   * In essence, adding a new expression should not result in changing the matching
   * set of condition in unpredictable ways (which can happen if groupings are
   * not applied correctly between combinations expressions with AND and OR).
   */
  public static Expression<?, String> combine(Expression<?, String> condition,
                                              Expression<?, String> expression,
                                              Filter.Op op) {
    if (condition instanceof And and) {
      return op == AND
           ? new And(and.context, and, expression)
           : new  Or(and.context, new GroupedExpression(and.context, and), expression);

    } else if (condition instanceof Or or) {
      return op == OR
           ? new  Or(or.context, or, expression)
           : new And(or.context, new GroupedExpression(or.context, or), expression);

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
