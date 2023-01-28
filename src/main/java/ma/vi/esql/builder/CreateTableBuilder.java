/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.builder;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.define.table.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * A builder for directly constructing `create table` ESQL statements. This
 * builder provides a fluent API and is easier to read when defining new
 * statements in Java code.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CreateTableBuilder extends CreateBuilder<CreateTable, CreateTableBuilder> {
  public CreateTableBuilder(Context context) {
    super(context);
  }

  @Override
  public CreateTable build() {
    return new CreateTable(context, name, dropUndefined, columns, constraints, new Metadata(context, metadata));
  }

  public CreateTableBuilder dropUndefined(boolean dropUndefined) {
    this.dropUndefined = dropUndefined;
    return this;
  }

  public CreateTableBuilder unique(String... fields) {
    return namedUnique(null, fields);
  }

  public CreateTableBuilder namedUnique(String constraintName, String... fields) {
    constraints.add(new UniqueConstraint(context, constraintName, name, Arrays.asList(fields)));
    return this;
  }

  public CreateTableBuilder primaryKey(String... fields) {
    return namedPrimaryKey(null, fields);
  }

  public CreateTableBuilder namedPrimaryKey(String constraintName, String... fields) {
    constraints.add(new PrimaryKeyConstraint(context, constraintName, name, Arrays.asList(fields)));
    return this;
  }

  public CreateTableBuilder check(String expression) {
    return check(null, expression);
  }

  public CreateTableBuilder check(String constraintName, String expression) {
    Parser parser = new Parser(context.structure);
    constraints.add(new CheckConstraint(context, constraintName, name, parser.parseExpression(expression)));
    return this;
  }

  public CreateTableBuilder foreignKey(List<String> sourceFields,
                                       String targetTable,
                                       List<String> targetFields) {
    return foreignKey(null, sourceFields, targetTable, targetFields, null, null, false);
  }

  public CreateTableBuilder foreignKey(List<String> sourceFields,
                                       String targetTable,
                                       List<String> targetFields,
                                       ConstraintDefinition.ForeignKeyChangeAction onUpdate,
                                       ConstraintDefinition.ForeignKeyChangeAction onDelete,
                                       boolean ignore) {
    return foreignKey(null, sourceFields, targetTable, targetFields, onUpdate, onDelete, ignore);
  }

  public CreateTableBuilder foreignKey(String sourceField,
                                       String targetTable,
                                       String targetField) {
    return foreignKey(sourceField, targetTable, targetField, null, null, false);
  }

  public CreateTableBuilder foreignKey(String sourceField,
                                       String targetTable,
                                       String targetField,
                                       ConstraintDefinition.ForeignKeyChangeAction onUpdate,
                                       ConstraintDefinition.ForeignKeyChangeAction onDelete,
                                       boolean ignore) {
    return foreignKey(null, sourceField, targetTable, targetField, onUpdate, onDelete, ignore);
  }

  public CreateTableBuilder foreignKey(String name,
                                       String sourceField,
                                       String targetTable,
                                       String targetField,
                                       ConstraintDefinition.ForeignKeyChangeAction onUpdate,
                                       ConstraintDefinition.ForeignKeyChangeAction onDelete,
                                       boolean ignore) {
    return foreignKey(name,
                      singletonList(sourceField),
                      targetTable,
                      singletonList(targetField),
                      onUpdate,
                      onDelete,
                      ignore);
  }

  public CreateTableBuilder foreignKey(String constraintName,
                                       List<String> sourceFields,
                                       String targetTable,
                                       List<String> targetFields,
                                       ConstraintDefinition.ForeignKeyChangeAction onUpdate,
                                       ConstraintDefinition.ForeignKeyChangeAction onDelete,
                                       boolean ignore) {
    constraints.add(new ForeignKeyConstraint(context,
                                             constraintName,
                                             name,
                                             sourceFields,
                                             targetTable,
                                             targetFields,
                                             1, 2,
                                             onUpdate,
                                             onDelete,
                                             ignore));
    return this;
  }

  private boolean dropUndefined = false;

  private final List<ConstraintDefinition> constraints = new ArrayList<>();
}
