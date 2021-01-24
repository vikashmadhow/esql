/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.Map;

import static java.lang.Double.parseDouble;

/**
 * A floating-point literal. This is kept in string form to
 * preserve precision.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class FloatingPointLiteral extends BaseLiteral<String> {
  public FloatingPointLiteral(Context context, String value) {
    super(context, value);
  }

  public FloatingPointLiteral(FloatingPointLiteral other) {
    super(other);
  }

  @Override
  public FloatingPointLiteral copy() {
    if (!copying()) {
      try {
        copying(true);
        return new FloatingPointLiteral(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.DoubleType;
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    return value;
  }

  @Override
  public Double value(Target target) {
    return parseDouble(value);
  }
}
