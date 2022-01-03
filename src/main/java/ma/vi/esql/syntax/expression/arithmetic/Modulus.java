/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.expression.Expression;

/**
 * Modulus (%) operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Modulus extends ArithmeticOperator {
  public Modulus(Context context,
                 Expression<?, String> expr1,
                 Expression<?, String> expr2) {
    super(context, "%", expr1, expr2);
  }

  public Modulus(Modulus other) {
    super(other);
  }

  @SafeVarargs
  public Modulus(Modulus other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Modulus copy() {
    return new Modulus(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Modulus copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Modulus(this, value, children);
  }
}
