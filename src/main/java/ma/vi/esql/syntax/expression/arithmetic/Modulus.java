/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.esql.syntax.Context;
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

  @Override
  public Modulus copy() {
    return new Modulus(this);
  }
}
