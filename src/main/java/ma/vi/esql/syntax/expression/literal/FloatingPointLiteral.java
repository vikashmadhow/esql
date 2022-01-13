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
import ma.vi.esql.syntax.Translatable;

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

  @SafeVarargs
  public FloatingPointLiteral(FloatingPointLiteral other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public FloatingPointLiteral copy() {
    return new FloatingPointLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public FloatingPointLiteral copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new FloatingPointLiteral(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.DoubleType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return value;
  }

  @Override
  public Double value(Translatable.Target target, EsqlPath path) {
    return parseDouble(value);
  }
}
