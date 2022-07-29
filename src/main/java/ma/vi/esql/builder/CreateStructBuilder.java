/*
 * Copyright (c) 2022 Vikash Madhow
 */

package ma.vi.esql.builder;

import ma.vi.base.lang.Builder;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.define.struct.CreateStruct;
import ma.vi.esql.syntax.define.table.ColumnDefinition;
import ma.vi.esql.syntax.define.table.DerivedColumnDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * A builder for directly constructing `create struct`s ESQL statements. This
 * builder provides a fluent API and is easier to read when defining new
 * statements in Java code.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CreateStructBuilder implements Builder<CreateStruct> {
  public CreateStructBuilder(Context context) {
    this.context = context;
  }

  @Override
  public CreateStruct build() {
    return new CreateStruct(context, name, columns, new Metadata(context, metadata));
  }

  public CreateStructBuilder name(String name) {
    this.name = name;
    return this;
  }

  public CreateStructBuilder metadata(String name, String expression) {
    Parser parser = new Parser(context.structure);
    this.metadata.add(new Attribute(context, name, parser.parseExpression(expression)));
    return this;
  }

  public CreateStructBuilder column(String name, String type, Attr... metadata) {
    return column(name, type, false, metadata);
  }

  public CreateStructBuilder column(String name, String type, boolean notNull, Attr... metadata) {
    return column(name, type, notNull, null, metadata);
  }

  public CreateStructBuilder column(String name, String type, boolean notNull,
                                    String defaultExpression, Attr... metadata) {
    Parser parser = new Parser(context.structure);
    this.columns.add(
        new ColumnDefinition(context, name, Types.typeOf(type),
                             notNull, defaultExpression == null
                     ? null
                     : parser.parseExpression(defaultExpression),
                             new Metadata(context,
                Stream.of(metadata)
                      .map(a -> new Attribute(context, a.name(), parser.parseExpression(a.expr())))
                      .collect(toList()))));
    return this;
  }

  public CreateStructBuilder derivedColumn(String name, String expression, Attr... metadata) {
    Parser parser = new Parser(context.structure);
    this.columns.add(
        new DerivedColumnDefinition(context, name, parser.parseExpression(expression),
                                    new Metadata(context,
                Stream.of(metadata)
                      .map(a -> new Attribute(context, a.name(), parser.parseExpression(a.expr())))
                      .collect(toList()))));
    return this;
  }

  private String name;

  private final List<ColumnDefinition> columns = new ArrayList<>();

  private final List<Attribute> metadata = new ArrayList<>();

  private final Context context;
}
