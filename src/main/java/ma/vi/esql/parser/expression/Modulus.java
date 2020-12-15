/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

public class Modulus extends ArithmeticOperator {
  public Modulus(Context context, Expression<?> expr1, Expression<?> expr2) {
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
