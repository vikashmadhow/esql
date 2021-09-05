/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.expression.Expression;

/**
 * Less-than operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LessThan extends ComparisonOperator {
  public LessThan(Context context,
                  Expression<?, String> expr1,
                  Expression<?, String> expr2) {
    super(context, "<", expr1, expr2);
  }

  public LessThan(LessThan other) {
    super(other);
  }

  @Override
  public LessThan copy() {
    return new LessThan(this);
  }
}
