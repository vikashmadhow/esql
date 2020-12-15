/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;

abstract class SingleSubExpression extends Expression<Expression<?>> {
  public SingleSubExpression(Context context, Expression<?> expr) {
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

  public Expression<?> expr() {
    return value;
  }
}