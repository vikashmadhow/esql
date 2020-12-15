/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.modify;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class InsertRow extends Expression<List<Expression<?>>> {
  public InsertRow(Context context, List<Expression<?>> values) {
    super(context, values);
  }

  public InsertRow(InsertRow other) {
    super(other);
  }

  @Override
  public InsertRow copy() {
    if (!copying()) {
      try {
        copying(true);
        return new InsertRow(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.BoolType;
  }

  @Override
  public String translate(Target target) {
    return values().stream()
                   .map(e -> e.translate(target))
                   .collect(joining(", ", "(", ")"));
  }

  public List<Expression<?>> values() {
    return value;
  }
}
