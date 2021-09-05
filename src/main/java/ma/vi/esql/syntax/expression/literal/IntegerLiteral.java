/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;

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
    return new IntegerLiteral(this);
  }

  @Override
  public Type type() {
    return Types.LongType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return String.valueOf(value);
  }
}
