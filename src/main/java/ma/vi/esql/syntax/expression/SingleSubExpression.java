/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Type;

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

  @Override
  public abstract SingleSubExpression copy();

  @Override
  public Type type() {
    return expr().type();
  }

  public Expression<?, String> expr() {
    return value;
  }
}