/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

public class Addition extends ArithmeticOperator {
  public Addition(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "+", expr1, expr2);
  }

  public Addition(Addition other) {
    super(other);
  }

  @Override
  public Addition copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Addition(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }
}
