/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

/**
 * The subtraction (-) operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Subtraction extends ArithmeticOperator {
  public Subtraction(Context context, Expression<?> expr1, Expression<?> expr2) {
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
