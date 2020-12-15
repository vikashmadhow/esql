/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

public class Division extends ArithmeticOperator {
  public Division(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "/", expr1, expr2);
  }

  public Division(Division other) {
    super(other);
  }

  @Override
  public Division copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Division(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }
}
