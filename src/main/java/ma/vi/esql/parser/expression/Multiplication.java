/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

public class Multiplication extends ArithmeticOperator {
  public Multiplication(Context context, Expression<?> expr1, Expression<?> expr2) {
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
