/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;
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
    return new UuidLiteral(this);
  }

  @Override
  public Type type() {
    return Types.UuidType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return switch(target) {
      case ESQL       -> "u'" + value + "'";
      case POSTGRESQL -> "'" + value + "'::uuid";
      default         -> '\'' + value.toString() + '\'';
    };
  }
}
