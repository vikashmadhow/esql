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
import ma.vi.esql.parser.modify.Insert;
import ma.vi.esql.parser.modify.InsertRow;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.parser.query.Select;
import ma.vi.esql.parser.query.SingleTableExpr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * A builder for directly constructing insert statements.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class InsertBuilder implements Builder<Insert> {
  public InsertBuilder(Context context) {
    this.context = context;
    this.parser = new Parser(context.structure);
  }

  @Override
  public Insert build() {
    return new Insert(context,
                      in,
                      insertColumns.isEmpty() ? null : insertColumns,
                      insertRows.isEmpty() ? null : insertRows,
                      defaultValues,
                      select,
                      returnMetadata.isEmpty() ? null : new Metadata(context, returnMetadata),
                      returnColumns.isEmpty() ? null : returnColumns);
  }


  public InsertBuilder insertColumns(String... columns) {
    insertColumns.addAll(Arrays.asList(columns));
    return this;
  }

  public InsertBuilder in(String tableName, String alias) {
    return in(new SingleTableExpr(context, tableName, alias));
  }

  public InsertBuilder in(SingleTableExpr in) {
    this.in = in;
    return this;
  }

  public InsertBuilder insertRow(String... expressions) {
    List<Expression<?>> exp = new ArrayList<>();
    for (String e: expressions) {
      exp.add(parser.parseExpression(e));
    }
    return insertRow(new InsertRow(context, exp));
  }

  public InsertBuilder insertRow(Expression<?>... expressions) {
    return insertRow(new InsertRow(context, Arrays.asList(expressions)));
  }

  public InsertBuilder insertRow(InsertRow row) {
    insertRows.add(row);
    return this;
  }

  public InsertBuilder defaultValues() {
    return defaultValues(true);
  }

  public InsertBuilder defaultValues(boolean defaultValues) {
    this.defaultValues = defaultValues;
    return this;
  }

  public InsertBuilder select(Select select) {
    this.select = select;
    return this;
  }

  public InsertBuilder returning(String expression, String alias, Attr... metadata) {
    return returning(expression == null ? null : parser.parseExpression(expression), alias, metadata);
  }

  public InsertBuilder returning(Expression<?> expression, String alias, Attr... metadata) {
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

  public InsertBuilder returningMetadata(String name, String expression) {
    this.returnMetadata.add(new Attribute(context, name, parser.parseExpression(expression)));
    return this;
  }

  private SingleTableExpr in;
  private final List<String> insertColumns = new ArrayList<>();
  private final List<InsertRow> insertRows = new ArrayList<>();
  private boolean defaultValues;
  private Select select;

  private final List<Attribute> returnMetadata = new ArrayList<>();
  private final List<Column> returnColumns = new ArrayList<>();

  private final Context context;
  private final Parser parser;
}
