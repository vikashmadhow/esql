/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.base.tuple.T2;
import ma.vi.base.util.Numbers;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.Arrays;

/**
 * An addition in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Addition extends ArithmeticOperator {
  public Addition(Context context,
                  Expression<?, String> expr1,
                  Expression<?, String> expr2) {
    super(context, "+", expr1, expr2);
  }

  public Addition(Addition other) {
    super(other);
  }

  @SafeVarargs
  public Addition(Addition other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Addition copy() {
    return new Addition(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Addition copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Addition(this, value, children);
  }

  @Override
  public Object postTransformExec(Target target, EsqlConnection esqlCon,
                                  EsqlPath path,
                                  PMap<String, Object> parameters, Environment env) {
    Object left = expr1().exec(target, esqlCon, path.add(expr1()), parameters, env);
    Object right = expr2().exec(target, esqlCon, path.add(expr2()), parameters, env);

    if (left instanceof Number ln
     && right instanceof Number rn) {
      if (Numbers.isReal(ln) || Numbers.isReal(rn)) {
        return ln.doubleValue() + rn.doubleValue();
      } else {
        return ln.longValue() + rn.longValue();
      }
    } else if (left instanceof String
            || right instanceof String) {
      String leftStr = left == null              ? "null"
                     : left.getClass().isArray() ? Arrays.toString((Object[])left)
                     : left.toString();
      String rightStr = right == null              ? "null"
                      : right.getClass().isArray() ? Arrays.toString((Object[])right)
                      : right.toString();
      return leftStr + rightStr;
    } else {
      throw new ExecutionException(this, "Incompatible types for " + op()
                                       + ": left " + left + ", right: " + right);
    }
  }
}
