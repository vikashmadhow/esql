/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.expression.DefaultValue;
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

  public GreaterThanOrEqual(GreaterThanOrEqual other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public GreaterThanOrEqual copy() {
    return new GreaterThanOrEqual(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public GreaterThanOrEqual copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new GreaterThanOrEqual(this, value, children);
  }
}
