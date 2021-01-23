/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.UUID;

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
    return switch(target) {
      case ESQL       -> "u'" + value + "'";
      case POSTGRESQL -> "'" + value + "'::uuid";
      default         -> '\'' + value.toString() + '\'';
    };
  }
}
