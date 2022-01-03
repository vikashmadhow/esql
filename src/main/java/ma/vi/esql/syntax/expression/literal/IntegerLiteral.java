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

  @SafeVarargs
  public IntegerLiteral(IntegerLiteral other, Long value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public IntegerLiteral copy() {
    return new IntegerLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public IntegerLiteral copy(Long value, T2<String, ? extends Esql<?, ?>>... children) {
    return new IntegerLiteral(this, value, children);
  }

  @Override
  public Type type(EsqlPath path) {
    return Types.LongType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return String.valueOf(value);
  }
}
