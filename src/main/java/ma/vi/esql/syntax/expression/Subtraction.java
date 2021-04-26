/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;

/**
 * The subtraction (-) operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Subtraction extends ArithmeticOperator {
  public Subtraction(Context context,
                     Expression<?, String> expr1,
                     Expression<?, String> expr2) {
    super(context, "-", expr1, expr2);
  }

  public Subtraction(Subtraction other) {
    super(other);
  }

  @Override
  public Subtraction copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Subtraction(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }
}
