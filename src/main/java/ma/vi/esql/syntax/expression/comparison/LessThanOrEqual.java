/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.base.util.Numbers;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;

import java.util.Date;

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

  @Override
  public Object postTransformExec(Target target, EsqlConnection esqlCon,
                                  EsqlPath path,
                                  Environment env) {
    Object left = expr1().exec(target, esqlCon, path.add(expr1()), env);
    Object right = expr2().exec(target, esqlCon, path.add(expr2()), env);

    if (left instanceof Number ln
     && right instanceof Number rn) {
      if (Numbers.isReal(ln) || Numbers.isReal(rn)) {
        return ln.doubleValue() <= rn.doubleValue();
      } else {
        return ln.longValue() <= rn.longValue();
      }
    } else if (left instanceof String
            || right instanceof String) {
      return String.valueOf(left).compareTo(String.valueOf(right)) <= 0;

    } else if (left instanceof Date d1
            && right instanceof Date d2) {
      return d1.equals(d2) || d1.before(d2);

    } else if (left instanceof Boolean b1
            && right instanceof Boolean b2) {
      return b2 || b1.equals(b2);

    } else {
      throw new ExecutionException(this, "Incompatible types for " + op()
                                       + ": left " + left + ", right: " + right);
    }
  }
}
