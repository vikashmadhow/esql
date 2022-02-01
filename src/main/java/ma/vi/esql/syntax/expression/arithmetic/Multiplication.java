/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.base.tuple.T2;
import ma.vi.base.util.Numbers;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.apache.commons.lang3.StringUtils;

/**
 * Multiplication operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Multiplication extends ArithmeticOperator {
  public Multiplication(Context context,
                        Expression<?, String> expr1,
                        Expression<?, String> expr2) {
    super(context, "*", expr1, expr2);
  }

  public Multiplication(Multiplication other) {
    super(other);
  }

  @SafeVarargs
  public Multiplication(Multiplication other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Multiplication copy() {
    return new Multiplication(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Multiplication copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Multiplication(this, value, children);
  }

  @Override
  public Object postTransformExec(EsqlConnection esqlCon,
                                  EsqlPath       path,
                                  Environment    env) {
    Object left = expr1().exec(esqlCon, path.add(expr1()), env);
    Object right = expr2().exec(esqlCon, path.add(expr2()), env);

    if (left instanceof Number ln
     && right instanceof Number rn) {
      if (Numbers.isReal(ln) || Numbers.isReal(rn)) {
        return ln.doubleValue() * rn.doubleValue();
      } else {
        return ln.longValue() * rn.longValue();
      }
    } else if (left instanceof String str
            && right instanceof Number num) {
      return StringUtils.repeat(str, num.intValue());
    } else {
      throw new ExecutionException("Incompatible types for " + op()
                                 + ": left " + left + ", right: " + right);
    }
  }
}
