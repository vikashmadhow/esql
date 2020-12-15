/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.UUID;

import static ma.vi.esql.parser.Translatable.Target.ESQL;

/**
 * A UUID literal in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UuidLiteral extends BaseLiteral<UUID> {
  public UuidLiteral(Context context, UUID value) {
    super(context, value);
  }

  public UuidLiteral(UuidLiteral other) {
    super(other);
  }

  @Override
  public UuidLiteral copy() {
    if (!copying()) {
      try {
        copying(true);
        return new UuidLiteral(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.UuidType;
  }

  @Override
  public String translate(Target target) {
    if (target == ESQL) {
      return "u'" + value + "'";
    } else {
      return '\'' + value.toString() + '\'';
    }
  }
}
