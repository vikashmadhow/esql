/*
 * Copyright (c) 2022 Vikash Madhow
 */

package ma.vi.esql.builder;

import ma.vi.base.lang.Builder;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Create;
import ma.vi.esql.syntax.define.Metadata;
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
public abstract class CreateBuilder<T extends Create,
                                    S extends CreateBuilder<T, S>> implements Builder<T> {
  public CreateBuilder(Context context) {
    this.context = context;
  }

  public S name(String name) {
    this.name = name;
    return (S)this;
  }

  public S metadata(String name, String expression) {
    Parser parser = new Parser(context.structure);
    this.metadata.add(new Attribute(context, name, parser.parseExpression(expression)));
    return (S)this;
  }

  public S column(String name, String type, Attr... metadata) {
    return column(name, type, false, metadata);
  }

  public S column(String name, String type, boolean notNull, Attr... metadata) {
    return column(name, type, notNull, null, metadata);
  }

  public S column(String name, String type, boolean notNull,
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
    return (S)this;
  }

  public S derivedColumn(String name, String expression, Attr... metadata) {
    Parser parser = new Parser(context.structure);
    this.columns.add(
        new DerivedColumnDefinition(context, name, parser.parseExpression(expression),
                                    new Metadata(context,
                Stream.of(metadata)
                      .map(a -> new Attribute(context, a.name(), parser.parseExpression(a.expr())))
                      .collect(toList()))));
    return (S)this;
  }

  protected String name;

  protected final List<ColumnDefinition> columns = new ArrayList<>();

  protected final List<Attribute> metadata = new ArrayList<>();

  protected final Context context;
}
