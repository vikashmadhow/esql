/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.builder;

import ma.vi.base.lang.Builder;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.literal.NullLiteral;
import ma.vi.esql.syntax.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static ma.vi.esql.semantic.type.Types.UnknownType;
import static ma.vi.esql.syntax.query.GroupBy.Type.*;

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

  public SelectBuilder distinctOn(Expression<?, ?> distinctOn) {
    this.distinctOn.add(distinctOn);
    return this;
  }

  public SelectBuilder metadata(String name, String expression) {
    this.metadata.add(new Attribute(context, name, parser.parseExpression(expression)));
    return this;
  }

  public SelectBuilder column(String expression, String alias, Attr... metadata) {
    return column(expression == null ? new NullLiteral(context)
                                     : parser.parseExpression(expression),
                  alias, metadata);
  }

  public SelectBuilder column(Expression<?, String> expression, String alias, Attr... metadata) {
    ColumnRef ref = expression.find(ColumnRef.class);
    if (alias == null) {
      alias = ColumnList.makeUnique("column",
                                    this.columns.stream().map(Column::name).collect(toSet()),
                                    1, true).a;
    }
    this.columns.add(
      new Column(context,
                 alias,
                 expression,
                 ref == null ? expression.computeType(new EsqlPath(expression)) : UnknownType,
                 metadata.length == 0
                   ? null
                   : new Metadata(context,
                                  Stream.of(metadata)
                                        .map(a -> new Attribute(context, a.name(), parser.parseExpression(a.expr())))
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

  public SelectBuilder join(String tableName, String alias, String onExpression, boolean lateral) {
    return join(new SingleTableExpr(context, tableName, alias), onExpression, lateral);
  }

  public SelectBuilder join(TableExpr right, String onExpression, boolean lateral) {
    return from(new JoinTableExpr(context, null, lateral, from, right, parser.parseExpression(onExpression)));
  }

  public SelectBuilder leftJoin(String tableName, String alias, String onExpression, boolean lateral) {
    return leftJoin(new SingleTableExpr(context, tableName, alias), onExpression, lateral);
  }

  public SelectBuilder leftJoin(TableExpr right, String onExpression, boolean lateral) {
    return from(new JoinTableExpr(context, "left", lateral, from, right, parser.parseExpression(onExpression)));
  }

  public SelectBuilder rightJoin(String tableName, String alias, String onExpression, boolean lateral) {
    return rightJoin(new SingleTableExpr(context, tableName, alias), onExpression, lateral);
  }

  public SelectBuilder rightJoin(TableExpr right, String onExpression, boolean lateral) {
    return from(new JoinTableExpr(context, "right", lateral, from, right, parser.parseExpression(onExpression)));
  }

  public SelectBuilder fullJoin(String tableName, String alias, String onExpression, boolean lateral) {
    return fullJoin(new SingleTableExpr(context, tableName, alias), onExpression, lateral);
  }

  public SelectBuilder fullJoin(TableExpr right, String onExpression, boolean lateral) {
    return from(new JoinTableExpr(context, "full", lateral, from, right, parser.parseExpression(onExpression)));
  }

  public SelectBuilder where(String expression) {
    return where(parser.parseExpression(expression));
  }

  public SelectBuilder where(Expression<?, String> expression) {
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

  public SelectBuilder orderBy(Expression<?, String> expression, String dir) {
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
  private final List<Expression<?, ?>> distinctOn = new ArrayList<>();
  private boolean explicit;
  private final List<Attribute> metadata = new ArrayList<>();
  private final List<Column> columns = new ArrayList<>();
  private TableExpr from;
  private Expression<?, String> where;
  private final List<Order> orderBy = new ArrayList<>();
  private final List<Expression<?, ?>> groupBy = new ArrayList<>();
  private GroupBy.Type groupType = Simple;
  private Expression<?, String> having;
  private Expression<?, String> offset;
  private Expression<?, String> limit;

  private final Context context;
  private final Parser parser;
}
