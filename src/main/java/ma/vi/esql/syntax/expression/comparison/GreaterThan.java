/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.expression.Expression;

/**
 * The greater-than operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class GreaterThan extends ComparisonOperator {
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
