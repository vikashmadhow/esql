/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.composable.Composable;
import ma.vi.esql.exec.composable.ComposableColumn;
import ma.vi.esql.exec.composable.ComposableFilter;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.*;
import ma.vi.esql.syntax.expression.logical.And;
import ma.vi.esql.syntax.expression.logical.Not;
import ma.vi.esql.syntax.expression.logical.Or;
import ma.vi.esql.syntax.modify.InsertRow;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static ma.vi.base.tuple.T2.of;
import static ma.vi.esql.exec.composable.Composable.Op.AND;
import static ma.vi.esql.exec.composable.Composable.Op.OR;
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
                boolean                  unfiltered,
                boolean                  explicit,
                boolean                  distinct,
                List<Expression<?, ?>>   distinctOn,
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
              of("unfiltered",  new Esql<>(context, unfiltered)),
              of("explicit",    new Esql<>(context, explicit)),
              of("distinct",    new Esql<>(context, distinct)),
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
  public Select column(ComposableColumn column,
                       boolean          firstColumn,
                       EsqlPath         path) {
    if (!getClass().equals(Select.class))
      /*
       * For subclasses, assume that column merging is already done by merging
       * of their children (this is true for the current known subclasses which
       * include `CompositeSelect` and `SelectExpression`).
       */
      return this;

    /*
     * Do not expand column list of selects inside expressions as the whole
     * expression is a single-value and expanding the column list will break the
     * query or not be of any use. The same applies to when select is used as an
     * insert value, or part of a column list.
     */
    if (path.hasAncestor(SelectExpression.class,
                         InsertRow       .class,
                         Column          .class,
                         FunctionCall    .class,
                         BinaryOperator  .class))
      return this;

    FilterResult result = null;
    Expression<?, String> resultExpr;
    if (column.table() == null) {
      /*
       * A column can be composed without specifying any target table; in such
       * cases, it is assumed that the required tables are already included in
       * the `from` clause of the select. All column references must be fully
       * and correctly qualified for this composition to work. If a column is
       * not qualified, the composition fails with an exception. If it is
       * qualified with a wrong qualifier (one that is not associated to a table
       * in the table list of the query), the composition is silently ignored.
       * This is because the composition may succeed in another part of the query
       * (such as when composing on a select containing an inner select or is
       * part of a set-composed select).
       */
      Parser parser = new Parser(context.structure);
      resultExpr = parser.parseExpression(column.expression());

      Set<String> aliases = tables().aliases();
      List<ColumnRef> refs = resultExpr.findAll(ColumnRef.class);
      if (refs.stream().anyMatch(r -> r.qualifier() == null))
        throw new IllegalArgumentException("Composing a column without a target table ("
                                          + column + ") requires that all column references "
                                          + "in the column expression contain a qualifier "
                                          + "(alias of the table containing the column)");

      if (refs.stream().anyMatch(r -> !aliases.contains(r.qualifier())))
        /*
         * Silently ignore composition when qualifier for any column is not in
         * the table list.
         */
        return this;

    } else {
      result = compose(tables(), column);
      if (result == null) {
        /*
         * A link from the current tables in the select expression to the table of
         * the column could not be found. The column might be embedded inside a
         * sub-select from which the current statement is selecting from. Attempt
         * to find a column to cover those cases, if any.
         */
        ColumnRef col = tables().findColumn(column.table(), column.expression());
        if (col == null) return this;
        result = new FilterResult(tables(), col, false);
      }
      resultExpr = result.expression();
    }

    boolean hasSameColumn = columns().stream().anyMatch(c -> c.expression().equals(resultExpr));
    if (hasSameColumn)
      return this;

    List<Column> cols = new ArrayList<>(columns());
    /*
     * Remove placeholder column used when composing columns into a query which
     * has no column list (since this is illegal in ESQL, the placeholder column
     * ___ is used to get past the parser).
     */
    cols.removeIf(c -> c.name().equals("___"));

    /*
     * Separate existing columns in aggregates (i.e. those using an aggregate
     * function (e.g. `sum`, `count`, etc.) without a window part (i.e. an `over`
     * definition), thus requiring a grouping clause; and non-aggregates.
     */
    List<Column> nonAggregates = cols.stream()
                                     .filter(c -> !aggregate(c.expression()))
                                     .collect(toCollection(ArrayList::new));
    List<Column> aggregates = cols.stream()
                                  .filter(c -> aggregate(c.expression()))
                                  .collect(toCollection(ArrayList::new));
    /*
     * Ensure that the new column has a unique name in the column list.
     */
    Set<String> colNames = cols.stream()
                               .map(Column::name)
                               .collect(Collectors.toSet());
    String name = column.name();
    if (name == null
     && resultExpr instanceof ColumnRef ref) {
      name = ref.columnName();
    }
    if (name == null) name = "column";
    name = Strings.makeUniqueSeq(colNames, name);

    Column newCol = new Column(context,
                               name,
                               resultExpr,
                               null,
                               column.metadata());
    if (aggregate(resultExpr)) aggregates.add(newCol);
    else                       nonAggregates.add(newCol);

    /*
     * Grouping is required if there are both aggregates and non-aggregates; check
     * that the existing grouping covers all non-aggregates; fix it otherwise by
     * including the missing non-aggregates.
     */
    GroupBy groupBy = groupBy();
    if (column.group() != null
    || (!nonAggregates.isEmpty() && !aggregates.isEmpty())) {
      /*
       * The group type (simple, rollup or cube) is the more general one if the
       * existing group type of the query is different from the one specified on
       * the column. `cube` is the most general group type, followed by `rollup`
       * and `simple`.
       */
      GroupBy.Type groupType = groupBy == null ? column.group() : groupBy.groupType();
      if (column.group() != null &&
          column.group().ordinal() > groupType.ordinal()) {
        groupType = column.group();
        groupBy = new GroupBy(context,
                              groupBy.groupBy(),
                              groupType);
      }

      List<Expression<?, ?>> groupExpressions = groupBy == null
                                              ? new ArrayList<>()
                                              : new ArrayList<>(groupBy.groupBy());
      boolean changed = false;
      for (Column col: nonAggregates) {
        List<Expression<?, ?>> colExprs = List.of(col.expression());
        boolean isWindow = col.expression() instanceof FunctionCall f && f.hasWindow();
        if (isWindow) {
          /*
           * A window function cannot be added to the `group by` clause; instead
           * we can extract all column references from the function call and add
           * them, if any, to the group list.
           */
          Set<Expression<?, ?>> colRefs = new LinkedHashSet<>();
          col.expression().forEach((e, p) -> {
            if (e instanceof ColumnRef c && !colRefs.contains(c)) colRefs.add(c);
            return true;
          });
          colExprs = new ArrayList<>(colRefs);
        }
        for (Expression<?, ?> colExpr: colExprs) {
          boolean exists = false;
          for (Expression<?, ?> groupExpr: groupExpressions) {
            if ((isWindow && groupExpr.contains(colExpr)) || groupExpr.equals(colExpr)) {
              exists = true;
              break;
            }
          }
          if (!exists) {
            groupExpressions.add(colExpr);
            changed = true;
          }
        }
      }
      if (changed) {
        groupBy = new GroupBy(context,
                              groupExpressions,
                              groupType != null ? groupType
                            : groupBy   == null ? GroupBy.Type.Simple
                            : groupBy.groupType());
      }
    }

    /*
     * Add order if specified.
     */
    List<Order> orderBy = orderBy();
    if (column.order() != null
     && column.order() != Composable.Order.NONE) {
      if (orderBy == null) orderBy = new ArrayList<>();
      else                 orderBy = new ArrayList<>(orderBy);
      orderBy.add(new Order(context,
                            resultExpr,
                            column.order() == Composable.Order.DESC ? "desc" : null));
    }

    boolean doNotPutDistinct = doNotPutDistinct(context.structure.database.target(), path);
    return new Select(context,
                      metadata(),
                      unfiltered(),
                      explicit(),
                      (result != null && result.hasReverseLinks && !doNotPutDistinct) || distinct(),
                      distinctOn(),
                      Stream.concat(nonAggregates.stream(), aggregates.stream()).toList(),
                      result == null ? tables() : result.tables,
                      where(),
                      groupBy,
                      having(),
                      orderBy,
                      offset(),
                      limit());
  }

  private static boolean aggregate(Esql<?, ?> expr) {
    if (expr.find(Select.class) != null) {
      return false;
    }
    List<FunctionCall> calls = expr.findAll(FunctionCall.class);
    return calls.stream().anyMatch(c ->  c.function().aggregate && !c.hasWindow());
  }

  @Override
  public Select filter(ComposableFilter filter, boolean firstFilter, EsqlPath path) {
    if (getClass().equals(Select.class)
     && tables() != null
     && !unfiltered()) {
      FilterResult result = filter(tables(), filter, firstFilter, where());
      if (result != null) {
        boolean doNotPutDistinct = doNotPutDistinct(context.structure.database.target(), path);
        return new Select(context,
                          metadata(),
                          unfiltered(),
                          explicit(),
                          (result.hasReverseLinks && !doNotPutDistinct) || distinct(),
                          distinctOn(),
                          columns(),
                          result.tables,
                          result.expression,
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

  /*
   * For recursive `WITH` query on SQL Server, `distinct` is not supported in
   * the first CTE. If this is the case, exclude putting distinct even when
   * there are reverse links on the path.
   */
  private static boolean doNotPutDistinct(Target target, EsqlPath path) {
    if (target == SQLSERVER) {
      With with = path.ancestor(With.class);
      if (with != null && with.recursive()) {
        CompositeSelect sel = path.ancestor(CompositeSelect.class);
        return sel == with.ctes().get(0).query();
      }
    }
    return false;
  }

  public record FilterResult(TableExpr             tables,
                             Expression<?, String> expression,
                             boolean               hasReverseLinks) {}

  public static FilterResult filter(TableExpr             tables,
                                    ComposableFilter      filter,
                                    boolean               firstFilter,
                                    Expression<?, String> where) {
    while (filter.children().length == 1) {
      filter = filter.children()[0];
    }
    FilterResult result = filter.children().length == 0
                        ? singleFilter(tables, filter)
                        : multipleFilters(tables, filter.op(), filter.exclude(), filter.children());
    if (result != null
     && result.expression() != null
     && where != null) {
      where = combine(firstFilter ? new GroupedExpression(where.context, where)
                                  : where,
                      result.expression(),
                      firstFilter && filter.table() == null ? AND : filter.op()); // combine the 1st filter when it has children
                                                                                  // with the 'AND' connective as the filter.op
                                                                                  // is for connective to use for the children.
      result = new FilterResult(result.tables(), where, result.hasReverseLinks());
    }
    return result;
  }

  private static FilterResult compose(TableExpr  tables,
                                      Composable composable) {
    TableExpr.ShortestPath shortest = tables.findShortestPath(composable);
    if (shortest != null) {
      TableExpr.AppliedShortestPath applied = tables.applyShortestPath(shortest, tables);
      Expression<?, String> expression = null;
      if (composable.expression() != null) {
        /*
         * Change column alias in composable expression, if needed.
         */
        Parser parser = new Parser(tables.context.structure);
        expression = parser.parseExpression(composable.expression());
        // if (!applied.targetAlias().equals(composable.alias())) {
          expression = (Expression<?, String>)expression.map((e, p) -> {
            if (e instanceof ColumnRef ref) {
              return new ColumnRef(e.context, applied.targetAlias(), ref.columnName(), ref.type());
            }
            return e;
          });
        // }
      }
      return new FilterResult(rotateLeft(applied.result()),
                              expression,
                              shortest.path().hasReverseLink());
    } else {
      return null;
    }
  }

  private static FilterResult singleFilter(TableExpr        tables,
                                           ComposableFilter filter) {
    FilterResult result = compose(tables, filter);
    if (result != null && filter.exclude()) {
      result = new FilterResult(result.tables,
                                new Not(tables.context, result.expression),
                                result.hasReverseLinks);
    }
    return result;
  }

  private static FilterResult multipleFilters(TableExpr     tables,
                                              Composable.Op filterOp,
                                              boolean       exclude,
                                              ComposableFilter...  filters) {
    FilterResult result = null;
    for (ComposableFilter filter: filters) {
      while (filter.children().length == 1) {
        filter = filter.children()[0];
      }
      FilterResult subResult = filter.children().length == 0
                             ? singleFilter(tables, filter)
                             : multipleFilters(tables, filter.op(), filter.exclude(), filter.children());

      if (subResult != null) {
        Expression<?, String> subCondition = subResult.expression();
        if (result == null) {
          result = new FilterResult(subResult.tables(),
                                    subCondition,
                                    subResult.hasReverseLinks());
        } else {
          /*
           * Merge result
           */
          Expression<?, String> condition = result.expression();
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
    if (result != null && result.expression() != null) {
      Expression<?, String> condition = result.expression();
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
                                              Composable.Op op) {
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

  public Boolean unfiltered() {
    return childValue("unfiltered");
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
