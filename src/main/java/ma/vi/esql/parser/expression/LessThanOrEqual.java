/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

/**
 * Less-than-or-equal operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LessThanOrEqual extends RelationalOperator {
  public LessThanOrEqual(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "<=", expr1, expr2);
  }

  public LessThanOrEqual(LessThanOrEqual other) {
    super(other);
  }

  @Override
  public LessThanOrEqual copy() {
    if (!copying()) {
      try {
        copying(true);
        return new LessThanOrEqual(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }
}
