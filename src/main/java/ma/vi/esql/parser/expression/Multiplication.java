/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

/**
 * Multiplication operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Multiplication extends ArithmeticOperator {
  public Multiplication(Context context,
                        Expression<?, String> expr1,
                        Expression<?, String> expr2) {
    super(context, "*", expr1, expr2);
  }

  public Multiplication(Multiplication other) {
    super(other);
  }

  @Override
  public Multiplication copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Multiplication(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }
}
