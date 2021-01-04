/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

/**
 * Less-than operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
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
