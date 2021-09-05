/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.expression.Expression;

/**
 * A greater-than-or-equal operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class GreaterThanOrEqual extends ComparisonOperator {
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
    return new GreaterThanOrEqual(this);
  }
}
