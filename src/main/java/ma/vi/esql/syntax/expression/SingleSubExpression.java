/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Esql;

/**
 * Abstract parent of ESQL expressions taking exactly one argument.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class SingleSubExpression extends Expression<Expression<?, String>, String> {
  public SingleSubExpression(Context context, Expression<?, String> expr) {
    super(context, expr);
  }

  public SingleSubExpression(SingleSubExpression other) {
    super(other);
  }

  public SingleSubExpression(SingleSubExpression other, Expression<?, String> value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract SingleSubExpression copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract SingleSubExpression copy(Expression<?, String> value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  public Type type() {
    return expr().type();
  }

  public Expression<?, String> expr() {
    return value;
  }
}