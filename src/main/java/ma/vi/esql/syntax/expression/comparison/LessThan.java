/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
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

  @SafeVarargs
  public LessThan(LessThan other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public LessThan copy() {
    return new LessThan(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public LessThan copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new LessThan(this, value, children);
  }
}
