/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.expression.Expression;

/**
 * Less-than-or-equal operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LessThanOrEqual extends ComparisonOperator {
  public LessThanOrEqual(Context context,
                         Expression<?, String> expr1,
                         Expression<?, String> expr2) {
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
