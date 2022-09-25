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
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.modify.UpdateSet;
import ma.vi.esql.syntax.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static ma.vi.esql.semantic.type.Types.UnknownType;

/**
 * A builder for directly constructing update statements.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UpdateBuilder implements Builder<Update> {
  public UpdateBuilder(Context context) {
    this.context = context;
    this.parser = new Parser(context.structure);
  }

  @Override
  public Update build() {
    return new Update(context,
                      updateTableAlias,
                      from,
                      new UpdateSet(context, set),
                      where,
                      returnMetadata.isEmpty() ? null : new Metadata(context, returnMetadata),
                      returnColumns.isEmpty() ? null : returnColumns);
  }

  public UpdateBuilder table(String updateTableAlias) {
    this.updateTableAlias = updateTableAlias;
    return this;
  }

  public UpdateBuilder set(String name, String expression) {
    return set(name, parser.parseExpression(expression));
  }

  public UpdateBuilder set(String name, Expression<?, String> expression) {
    this.returnMetadata.add(new Attribute(context, name, expression));
    return this;
  }

  public UpdateBuilder from(TableExpr from) {
    this.from = from;
    return this;
  }

  public UpdateBuilder from(String tableName, String alias) {
    return from(new SingleTableExpr(context, tableName, alias));
  }

  public UpdateBuilder from(Select select, String alias) {
    return from(new SelectTableExpr(context, select, alias));
  }

  public UpdateBuilder times(String tableName, String alias) {
    return times(new SingleTableExpr(context, tableName, alias));
  }

  public UpdateBuilder times(TableExpr right) {
    return from(new CrossProductTableExpr(context, from, right));
  }

  public UpdateBuilder join(String tableName, String alias, String onExpression, boolean lateral) {
    return join(new SingleTableExpr(context, tableName, alias), onExpression, lateral);
  }

  public UpdateBuilder join(TableExpr right, String onExpression, boolean lateral) {
    return from(new JoinTableExpr(context, null, lateral, from, right, parser.parseExpression(onExpression)));
  }

  public UpdateBuilder leftJoin(String tableName, String alias, String onExpression, boolean lateral) {
    return leftJoin(new SingleTableExpr(context, tableName, alias), onExpression, lateral);
  }

  public UpdateBuilder leftJoin(TableExpr right, String onExpression, boolean lateral) {
    return from(new JoinTableExpr(context, "left", lateral, from, right, parser.parseExpression(onExpression)));
  }

  public UpdateBuilder rightJoin(String tableName, String alias, String onExpression, boolean lateral) {
    return rightJoin(new SingleTableExpr(context, tableName, alias), onExpression, lateral);
  }

  public UpdateBuilder rightJoin(TableExpr right, String onExpression, boolean lateral) {
    return from(new JoinTableExpr(context, "right", lateral, from, right, parser.parseExpression(onExpression)));
  }

  public UpdateBuilder fullJoin(String tableName, String alias, String onExpression, boolean lateral) {
    return fullJoin(new SingleTableExpr(context, tableName, alias), onExpression, lateral);
  }

  public UpdateBuilder fullJoin(TableExpr right, String onExpression, boolean lateral) {
    return from(new JoinTableExpr(context, "full", lateral, from, right, parser.parseExpression(onExpression)));
  }

  public UpdateBuilder where(String expression) {
    return where(parser.parseExpression(expression));
  }

  public UpdateBuilder where(Expression<?, String> expression) {
    this.where = expression;
    return this;
  }

  public UpdateBuilder returning(String expression, String alias, Attr... metadata) {
    return returning(expression == null ? null : parser.parseExpression(expression), alias, metadata);
  }

  public UpdateBuilder returning(Expression<?, String> expression, String alias, Attr... metadata) {
    ColumnRef ref = expression.find(ColumnRef.class);
    this.returnColumns.add(
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

  public UpdateBuilder returningMetadata(String name, String expression) {
    this.returnMetadata.add(new Attribute(context, name, parser.parseExpression(expression)));
    return this;
  }

  private String updateTableAlias;
  private TableExpr from;

  private final List<Attribute> set = new ArrayList<>();

  private Expression<?, String> where;

  private final List<Attribute> returnMetadata = new ArrayList<>();
  private final List<Column> returnColumns = new ArrayList<>();

  public final Context context;
  public final Parser parser;
}
