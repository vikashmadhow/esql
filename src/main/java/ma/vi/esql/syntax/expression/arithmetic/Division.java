/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.expression.Expression;

/**
 * The division operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Division extends ArithmeticOperator {
  public Division(Context context,
                  Expression<?, String> expr1,
                  Expression<?, String> expr2) {
    super(context, "/", expr1, expr2);
  }

  public Division(Division other) {
    super(other);
  }

  @Override
  public Division copy() {
    return new Division(this);
  }
}
