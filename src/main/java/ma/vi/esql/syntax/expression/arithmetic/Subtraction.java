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

/**
 * The subtraction (-) operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Subtraction extends ArithmeticOperator {
  public Subtraction(Context context,
                     Expression<?, String> expr1,
                     Expression<?, String> expr2) {
    super(context, "-", expr1, expr2);
  }

  public Subtraction(Subtraction other) {
    super(other);
  }

  @SafeVarargs
  public Subtraction(Subtraction other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Subtraction copy() {
    return new Subtraction(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Subtraction copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Subtraction(this, value, children);
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
        return ln.doubleValue() - rn.doubleValue();
      } else {
        return ln.longValue() - rn.longValue();
      }
    } else {
      throw new ExecutionException(this, "Incompatible types for " + op()
                                       + ": left " + left + ", right: " + right);
    }
  }
}
