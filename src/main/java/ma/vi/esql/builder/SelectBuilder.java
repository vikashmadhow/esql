/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.builder;

import ma.vi.base.lang.Builder;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.define.GroupBy;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static ma.vi.esql.parser.define.GroupBy.Type.*;

/**
 * A builder for directly constructing select statements.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SelectBuilder implements Builder<Select> {
  public SelectBuilder(Context context) {
    this.context = context;
    this.parser = new Parser(context.structure);
  }

  @Override
  public Select build() {
    return new Select(context,
                      metadata.isEmpty() ? null : new Metadata(context, metadata),
                      distinct, distinctOn, explicit, columns, from, where,
                      groupBy.isEmpty() ? null : new GroupBy(context, groupBy, groupType),
                      having, orderBy, offset, limit);
  }

  public SelectBuilder distinct(boolean distinct) {
    this.distinct = distinct;
    return this;
  }

  public SelectBuilder explicit(boolean explicit) {
    this.explicit = explicit;
    return this;
  }

  public SelectBuilder distinctOn(Expression<?> distinctOn) {
    this.distinctOn.add(distinctOn);
    return this;
  }

  public SelectBuilder metadata(String name, String expression) {
    this.metadata.add(new Attribute(context, name, parser.parseExpression(expression)));
    return this;
  }

  public SelectBuilder column(String expression, String alias, Attr... metadata) {
    return column(expression == null ? null : parser.parseExpression(expression), alias, metadata);
  }

  public SelectBuilder column(Expression<?> expression, String alias, Attr... metadata) {
    this.columns.add(
      new Column(context,
                 alias,
                 expression,
                 metadata.length == 0
                   ? null
                   : new Metadata(context,
                                  Stream.of(metadata)
                                        .map(a -> new Attribute(context, a.name, parser.parseExpression(a.expr)))
                                        .collect(toList()))));
    return this;
  }

  public SelectBuilder starColumn(String qualifier) {
    this.columns.add(new StarColumn(context, qualifier));
    return this;
  }

  public SelectBuilder from(TableExpr from) {
    this.from = from;
    return this;
  }

  public SelectBuilder from(String tableName, String alias) {
    return from(new SingleTableExpr(context, tableName, alias));
  }

  public SelectBuilder from(Select select, String alias) {
    return from(new SelectTableExpr(context, select, alias));
  }

  public SelectBuilder times(String tableName, String alias) {
    return times(new SingleTableExpr(context, tableName, alias));
  }

  public SelectBuilder times(TableExpr right) {
    return from(new CrossProductTableExpr(context, from, right));
  }

  public SelectBuilder join(String tableName, String alias, String onExpression) {
    return join(new SingleTableExpr(context, tableName, alias), onExpression);
  }

  public SelectBuilder join(TableExpr right, String onExpression) {
    return from(new JoinTableExpr(context, from, null, right, parser.parseExpression(onExpression)));
  }

  public SelectBuilder leftJoin(String tableName, String alias, String onExpression) {
    return leftJoin(new SingleTableExpr(context, tableName, alias), onExpression);
  }

  public SelectBuilder leftJoin(TableExpr right, String onExpression) {
    return from(new JoinTableExpr(context, from, "left", right, parser.parseExpression(onExpression)));
  }

  public SelectBuilder rightJoin(String tableName, String alias, String onExpression) {
    return rightJoin(new SingleTableExpr(context, tableName, alias), onExpression);
  }

  public SelectBuilder rightJoin(TableExpr right, String onExpression) {
    return from(new JoinTableExpr(context, from, "right", right, parser.parseExpression(onExpression)));
  }

  public SelectBuilder fullJoin(String tableName, String alias, String onExpression) {
    return fullJoin(new SingleTableExpr(context, tableName, alias), onExpression);
  }

  public SelectBuilder fullJoin(TableExpr right, String onExpression) {
    return from(new JoinTableExpr(context, from, "full", right, parser.parseExpression(onExpression)));
  }

  public SelectBuilder where(String expression) {
    return where(parser.parseExpression(expression));
  }

  public SelectBuilder where(Expression<?> expression) {
    this.where = expression;
    return this;
  }

  public SelectBuilder groupBy(String expression) {
    groupBy.add(parser.parseExpression(expression));
    return this;
  }

  public SelectBuilder rollup() {
    groupType = Rollup;
    return this;
  }

  public SelectBuilder cube() {
    groupType = Cube;
    return this;
  }

  public SelectBuilder having(String expression) {
    having = parser.parseExpression(expression);
    return this;
  }

  public SelectBuilder orderBy(String expression) {
    orderBy.add(new Order(context, parser.parseExpression(expression), null));
    return this;
  }

  public SelectBuilder orderBy(String expression, String dir) {
    return orderBy(parser.parseExpression(expression), dir);
  }

  public SelectBuilder orderBy(Expression<?> expression, String dir) {
    orderBy.add(new Order(context, expression, dir));
    return this;
  }

  public SelectBuilder offset(String expression) {
    offset = parser.parseExpression(expression);
    return this;
  }

  public SelectBuilder limit(String expression) {
    limit = parser.parseExpression(expression);
    return this;
  }

  private boolean distinct;
  private final List<Expression<?>> distinctOn = new ArrayList<>();
  private boolean explicit;
  private final List<Attribute> metadata = new ArrayList<>();
  private final List<Column> columns = new ArrayList<>();
  private TableExpr from;
  private Expression<?> where;
  private final List<Order> orderBy = new ArrayList<>();
  private final List<Expression<?>> groupBy = new ArrayList<>();
  private GroupBy.Type groupType = Simple;
  private Expression<?> having;
  private Expression<?> offset;
  private Expression<?> limit;

  private final Context context;
  private final Parser parser;
}
