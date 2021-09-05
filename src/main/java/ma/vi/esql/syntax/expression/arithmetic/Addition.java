/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.expression.Expression;

/**
 * An addition in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Addition extends ArithmeticOperator {
  public Addition(Context context,
                  Expression<?, String> expr1,
                  Expression<?, String> expr2) {
    super(context, "+", expr1, expr2);
  }

  public Addition(Addition other) {
    super(other);
  }

  @Override
  public Addition copy() {
    return new Addition(this);
  }
}
