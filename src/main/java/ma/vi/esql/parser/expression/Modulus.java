/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

/**
 * Modulus (%) operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Modulus extends ArithmeticOperator {
  public Modulus(Context context,
                 Expression<?, String> expr1,
                 Expression<?, String> expr2) {
    super(context, "%", expr1, expr2);
  }

  public Modulus(Modulus other) {
    super(other);
  }

  @Override
  public Modulus copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Modulus(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }
}
