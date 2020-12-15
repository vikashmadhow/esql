/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

public class LessThan extends RelationalOperator {
  public LessThan(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "<", expr1, expr2);
  }

  public LessThan(LessThan other) {
    super(other);
  }

  @Override
  public LessThan copy() {
    if (!copying()) {
      try {
        copying(true);
        return new LessThan(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }
}
