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
public class CreateStructBuilder extends CreateBuilder<CreateStruct, CreateStructBuilder> {
  public CreateStructBuilder(Context context) {
    super(context);
  }

  @Override
  public CreateStruct build() {
    return new CreateStruct(context, name, columns, new Metadata(context, metadata));
  }
}
