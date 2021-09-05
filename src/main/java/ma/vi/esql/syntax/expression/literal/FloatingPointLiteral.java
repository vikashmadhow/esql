/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
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

  @Override
  public FloatingPointLiteral copy() {
    return new FloatingPointLiteral(this);
  }

  @Override
  public Type type() {
    return Types.DoubleType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return value;
  }

  @Override
  public Double value(Translatable.Target target) {
    return parseDouble(value);
  }
}
