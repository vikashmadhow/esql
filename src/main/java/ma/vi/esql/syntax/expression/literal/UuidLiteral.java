/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

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

  public UuidLiteral(UuidLiteral other, UUID value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public UuidLiteral copy() {
    return new UuidLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public UuidLiteral copy(UUID value, T2<String, ? extends Esql<?, ?>>... children) {
    return new UuidLiteral(this, value, children);
  }

  @Override
  public Type type(EsqlPath path) {
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

  public static final String HEX_DIGIT = "[a-fA-F0-9]";

  public static final Pattern PATTERN = Pattern.compile(HEX_DIGIT + "{8}-" +
                                                        HEX_DIGIT + "{4}-" +
                                                        HEX_DIGIT + "{4}-" +
                                                        HEX_DIGIT + "{4}-" +
                                                        HEX_DIGIT + "{12}");
}
