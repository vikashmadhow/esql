/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;

/**
 * A greater-than-or-equal operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class GreaterThanOrEqual extends RelationalOperator {
  public GreaterThanOrEqual(Context context,
                            Expression<?, String> expr1,
                            Expression<?, String> expr2) {
    super(context, ">=", expr1, expr2);
  }

  public GreaterThanOrEqual(GreaterThanOrEqual other) {
    super(other);
  }

  @Override
  public GreaterThanOrEqual copy() {
    if (!copying()) {
      try {
        copying(true);
        return new GreaterThanOrEqual(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }
}
