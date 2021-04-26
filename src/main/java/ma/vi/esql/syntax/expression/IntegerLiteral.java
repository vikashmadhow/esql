/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.Map;

/**
 * An integer in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
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
  protected String trans(Target target, Map<String, Object> parameters) {
    return String.valueOf(value);
  }
}
