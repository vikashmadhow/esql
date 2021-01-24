/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;

import java.util.Map;

import static ma.vi.esql.type.Types.BoolType;

/**
 * A boolean literal in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class BooleanLiteral extends BaseLiteral<Boolean> {
  public BooleanLiteral(Context context, Boolean value) {
    super(context, value);
  }

  public BooleanLiteral(BooleanLiteral other) {
    super(other);
  }

  @Override
  public BooleanLiteral copy() {
    if (!copying()) {
      try {
        copying(true);
        return new BooleanLiteral(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return BoolType;
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    if (target == Target.SQLSERVER) {
      return value ? "1" : "0";
    }
    return String.valueOf(value);
  }

  @Override
  public Boolean value(Target target) {
    return value;
  }
}
