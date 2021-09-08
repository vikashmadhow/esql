/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.DefaultValue;

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

  public BooleanLiteral(BooleanLiteral other, Boolean value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public BooleanLiteral copy() {
    return new BooleanLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public BooleanLiteral copy(Boolean value, T2<String, ? extends Esql<?, ?>>... children) {
    return new BooleanLiteral(this, value, children);
  }

  @Override
  public Type type(EsqlPath path) {
    return BoolType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
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
