/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Translatable;

import java.util.Map;

import static ma.vi.esql.semantic.type.Types.BoolType;

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
  protected String trans(Translatable.Target target, Map<String, Object> parameters) {
    if (target == Translatable.Target.SQLSERVER) {
      return value ? "1" : "0";
    }
    return String.valueOf(value);
  }

  @Override
  public Boolean value(Translatable.Target target) {
    return value;
  }
}
