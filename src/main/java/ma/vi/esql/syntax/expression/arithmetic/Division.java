/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.expression.Expression;

/**
 * The division operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Division extends ArithmeticOperator {
  public Division(Context context,
                  Expression<?, String> expr1,
                  Expression<?, String> expr2) {
    super(context, "/", expr1, expr2);
  }

  public Division(Division other) {
    super(other);
  }

  public Division(Division other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Division copy() {
    return new Division(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Division copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Division(this, value, children);
  }
}
