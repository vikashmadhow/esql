/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

/**
 * The greater-than operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class GreaterThan extends RelationalOperator {
  public GreaterThan(Context context,
                     Expression<?, String> expr1,
                     Expression<?, String> expr2) {
    super(context, ">", expr1, expr2);
  }

  public GreaterThan(GreaterThan other) {
    super(other);
  }

  @Override
  public GreaterThan copy() {
    if (!copying()) {
      try {
        copying(true);
        return new GreaterThan(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }
}
