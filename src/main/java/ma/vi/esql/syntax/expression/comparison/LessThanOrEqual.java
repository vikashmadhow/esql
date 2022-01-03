/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
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

  @SafeVarargs
  public LessThanOrEqual(LessThanOrEqual other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public LessThanOrEqual copy() {
    return new LessThanOrEqual(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public LessThanOrEqual copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new LessThanOrEqual(this, value, children);
  }
}
