/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

public class IntegerLiteral extends BaseLiteral<Long> {
  public IntegerLiteral(Context context, Long value) {
    super(context, value);
  }

  public IntegerLiteral(IntegerLiteral other) {
    super(other);
  }

  @Override
  public IntegerLiteral copy() {
    if (!copying()) {
      try {
        copying(true);
        return new IntegerLiteral(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.LongType;
  }

  @Override
  public String translate(Target target) {
    return String.valueOf(value);
  }
}
