/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.builder;

import ma.vi.base.lang.Builder;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * A builder for directly constructing delete statements.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DeleteBuilder implements Builder<Delete> {
  public DeleteBuilder(Context context) {
    this.context = context;
    this.parser = new Parser(context.structure);
  }

  @Override
  public Delete build() {
    return new Delete(context,
                      deleteTableAlias,
                      from,
                      where,
                      returnMetadata.isEmpty() ? null : new Metadata(context, returnMetadata),
                      returnColumns.isEmpty() ? null : returnColumns);
  }

  public DeleteBuilder table(String deleteTableAlias) {
    this.deleteTableAlias = deleteTableAlias;
    return this;
  }

  public DeleteBuilder from(TableExpr from) {
    this.from = from;
    return this;
  }

  public DeleteBuilder from(String tableName, String alias) {
    return from(new SingleTableExpr(context, tableName, alias));
  }

  public DeleteBuilder from(Select select, String alias) {
    return from(new SelectTableExpr(context, select, alias));
  }

  public DeleteBuilder times(String tableName, String alias) {
    return times(new SingleTableExpr(context, tableName, alias));
  }

  public DeleteBuilder times(TableExpr right) {
    return from(new CrossProductTableExpr(context, from, right));
  }

  public DeleteBuilder join(String tableName, String alias, String onExpression) {
    return join(new SingleTableExpr(context, tableName, alias), onExpression);
  }

  public DeleteBuilder join(TableExpr right, String onExpression) {
    return from(new JoinTableExpr(context, from, null, right, parser.parseExpression(onExpression)));
  }

  public DeleteBuilder leftJoin(String tableName, String alias, String onExpression) {
    return leftJoin(new SingleTableExpr(context, tableName, alias), onExpression);
  }

  public DeleteBuilder leftJoin(TableExpr right, String onExpression) {
    return from(new JoinTableExpr(context, from, "left", right, parser.parseExpression(onExpression)));
  }

  public DeleteBuilder rightJoin(String tableName, String alias, String onExpression) {
    return rightJoin(new SingleTableExpr(context, tableName, alias), onExpression);
  }

  public DeleteBuilder rightJoin(TableExpr right, String onExpression) {
    return from(new JoinTableExpr(context, from, "right", right, parser.parseExpression(onExpression)));
  }

  public DeleteBuilder fullJoin(String tableName, String alias, String onExpression) {
    return fullJoin(new SingleTableExpr(context, tableName, alias), onExpression);
  }

  public DeleteBuilder fullJoin(TableExpr right, String onExpression) {
    return from(new JoinTableExpr(context, from, "full", right, parser.parseExpression(onExpression)));
  }

  public DeleteBuilder where(String expression) {
    return where(parser.parseExpression(expression));
  }

  public DeleteBuilder where(Expression<?> expression) {
    this.where = expression;
    return this;
  }

  public DeleteBuilder returning(String expression, String alias, Attr... metadata) {
    return returning(expression == null ? null : parser.parseExpression(expression), alias, metadata);
  }

  public DeleteBuilder returning(Expression<?> expression, String alias, Attr... metadata) {
    this.returnColumns.add(
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

  public DeleteBuilder returningMetadata(String name, String expression) {
    this.returnMetadata.add(new Attribute(context, name, parser.parseExpression(expression)));
    return this;
  }

  private String deleteTableAlias;

  private TableExpr from;
  private Expression<?> where;

  private final List<Attribute> returnMetadata = new ArrayList<>();
  private final List<Column> returnColumns = new ArrayList<>();

  private final Context context;
  private final Parser parser;
}
