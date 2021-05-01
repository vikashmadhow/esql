/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.builder;

import ma.vi.base.lang.Builder;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.define.*;
import ma.vi.esql.semantic.type.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * A builder for directly constructing create tables ESQL statement.
 * This builder provides a fluent API and is easier to read when
 * defining new statements in Java code.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CreateTableBuilder implements Builder<CreateTable> {
  public CreateTableBuilder(Context context) {
    this.context = context;
  }

  @Override
  public CreateTable build() {
    return new CreateTable(context, name, dropUndefined, columns, constraints, new Metadata(context, metadata));
  }

  public CreateTableBuilder name(String name) {
    this.name = name;
    return this;
  }

  public CreateTableBuilder dropUndefined(boolean dropUndefined) {
    this.dropUndefined = dropUndefined;
    return this;
  }

  public CreateTableBuilder metadata(String name, String expression) {
    Parser parser = new Parser(context.structure);
    this.metadata.add(new Attribute(context, name, parser.parseExpression(expression)));
    return this;
  }

  public CreateTableBuilder column(String name, String type, Attr... metadata) {
    return column(name, type, false, null, metadata);
  }

  public CreateTableBuilder column(String name, String type, boolean notNull,
                                   String defaultExpression, Attr... metadata) {
    Parser parser = new Parser(context.structure);
    this.columns.add(
        new ColumnDefinition(context, name, Types.typeOf(type),
                             notNull, defaultExpression == null
                     ? null
                     : parser.parseExpression(defaultExpression),
                             new Metadata(context,
                Stream.of(metadata)
                      .map(a -> new Attribute(context, a.name, parser.parseExpression(a.expr)))
                      .collect(toList()))));
    return this;
  }

  public CreateTableBuilder derivedColumn(String name, String expression, Attr... metadata) {
    Parser parser = new Parser(context.structure);
    this.columns.add(
        new DerivedColumnDefinition(context, name, parser.parseExpression(expression),
            new Metadata(context,
                Stream.of(metadata)
                      .map(a -> new Attribute(context, a.name, parser.parseExpression(a.expr)))
                      .collect(toList()))));
    return this;
  }

  public CreateTableBuilder unique(String... fields) {
    return namedUnique(null, fields);
  }

  public CreateTableBuilder namedUnique(String name, String... fields) {
    constraints.add(new UniqueConstraint(context, name, Arrays.asList(fields)));
    return this;
  }

  public CreateTableBuilder primaryKey(String... fields) {
    return namedPrimaryKey(null, fields);
  }

  public CreateTableBuilder namedPrimaryKey(String name, String... fields) {
    constraints.add(new PrimaryKeyConstraint(context, name, Arrays.asList(fields)));
    return this;
  }

  public CreateTableBuilder check(String expression) {
    return check(null, expression);
  }

  public CreateTableBuilder check(String name, String expression) {
    Parser parser = new Parser(context.structure);
    constraints.add(new CheckConstraint(context, name, parser.parseExpression(expression)));
    return this;
  }

  public CreateTableBuilder foreignKey(List<String> sourceFields,
                                       String targetTable,
                                       List<String> targetFields) {
    return foreignKey(null, sourceFields, targetTable, targetFields, null, null);
  }

  public CreateTableBuilder foreignKey(List<String> sourceFields,
                                       String targetTable,
                                       List<String> targetFields,
                                       ConstraintDefinition.ForeignKeyChangeAction onUpdate,
                                       ConstraintDefinition.ForeignKeyChangeAction onDelete) {
    return foreignKey(null, sourceFields, targetTable, targetFields, onUpdate, onDelete);
  }

  public CreateTableBuilder foreignKey(String sourceField,
                                       String targetTable,
                                       String targetField) {
    return foreignKey(sourceField, targetTable, targetField, null, null);
  }

  public CreateTableBuilder foreignKey(String sourceField,
                                       String targetTable,
                                       String targetField,
                                       ConstraintDefinition.ForeignKeyChangeAction onUpdate,
                                       ConstraintDefinition.ForeignKeyChangeAction onDelete) {
    return foreignKey(null, sourceField, targetTable, targetField, onUpdate, onDelete);
  }

  public CreateTableBuilder foreignKey(String name,
                                       String sourceField,
                                       String targetTable,
                                       String targetField,
                                       ConstraintDefinition.ForeignKeyChangeAction onUpdate,
                                       ConstraintDefinition.ForeignKeyChangeAction onDelete) {
    return foreignKey(name,
                      singletonList(sourceField),
                      targetTable,
                      singletonList(targetField),
                      onUpdate,
                      onDelete);
  }

  public CreateTableBuilder foreignKey(String name,
                                       List<String> sourceFields,
                                       String targetTable,
                                       List<String> targetFields,
                                       ConstraintDefinition.ForeignKeyChangeAction onUpdate,
                                       ConstraintDefinition.ForeignKeyChangeAction onDelete) {
    constraints.add(new ForeignKeyConstraint(context,
                                             name,
                                             sourceFields,
                                             targetTable,
                                             targetFields,
                                             1, 2,
                                             onUpdate,
                                             onDelete));
    return this;
  }

  private String name;
  private boolean dropUndefined = false;
  private List<ColumnDefinition> columns = new ArrayList<>();
  private List<ConstraintDefinition> constraints = new ArrayList<>();
  private List<Attribute> metadata = new ArrayList<>();
  private final Context context;
}
