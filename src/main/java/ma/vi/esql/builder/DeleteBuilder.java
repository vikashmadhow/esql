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
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static ma.vi.esql.semantic.type.Types.UnknownType;

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
                      unfiltered,
                      deleteTableAlias,
                      from,
                      where,
                      returnMetadata.isEmpty() ? null : new Metadata(context, returnMetadata),
                      returnColumns.isEmpty() ? null : returnColumns);
  }

  public DeleteBuilder unfiltered(boolean unfiltered) {
    this.unfiltered = unfiltered;
    return this;
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

  public DeleteBuilder join(String tableName, String alias, String onExpression, boolean lateral) {
    return join(new SingleTableExpr(context, tableName, alias), onExpression, lateral);
  }

  public DeleteBuilder join(TableExpr right, String onExpression, boolean lateral) {
    return from(new JoinTableExpr(context, null, lateral, from, right, parser.parseExpression(onExpression)));
  }

  public DeleteBuilder leftJoin(String tableName, String alias, String onExpression, boolean lateral) {
    return leftJoin(new SingleTableExpr(context, tableName, alias), onExpression, lateral);
  }

  public DeleteBuilder leftJoin(TableExpr right, String onExpression, boolean lateral) {
    return from(new JoinTableExpr(context, "left", lateral, from, right, parser.parseExpression(onExpression)));
  }

  public DeleteBuilder rightJoin(String tableName, String alias, String onExpression, boolean lateral) {
    return rightJoin(new SingleTableExpr(context, tableName, alias), onExpression, lateral);
  }

  public DeleteBuilder rightJoin(TableExpr right, String onExpression, boolean lateral) {
    return from(new JoinTableExpr(context, "right", lateral, from, right, parser.parseExpression(onExpression)));
  }

  public DeleteBuilder fullJoin(String tableName, String alias, String onExpression, boolean lateral) {
    return fullJoin(new SingleTableExpr(context, tableName, alias), onExpression, lateral);
  }

  public DeleteBuilder fullJoin(TableExpr right, String onExpression, boolean lateral) {
    return from(new JoinTableExpr(context, "full", lateral, from, right, parser.parseExpression(onExpression)));
  }

  public DeleteBuilder where(String expression) {
    return where(parser.parseExpression(expression));
  }

  public DeleteBuilder where(Expression<?, String> expression) {
    this.where = expression;
    return this;
  }

  public DeleteBuilder returning(String expression, String alias, Attr... metadata) {
    return returning(expression == null ? null : parser.parseExpression(expression), alias, metadata);
  }

  public DeleteBuilder returning(Expression<?, String> expression, String alias, Attr... metadata) {
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

  public DeleteBuilder returningMetadata(String name, String expression) {
    this.returnMetadata.add(new Attribute(context, name, parser.parseExpression(expression)));
    return this;
  }

  private boolean unfiltered;
  private String deleteTableAlias;

  private TableExpr from;
  private Expression<?, String> where;

  private final List<Attribute> returnMetadata = new ArrayList<>();
  private final List<Column> returnColumns = new ArrayList<>();

  public final Context context;
  public final Parser parser;
}
