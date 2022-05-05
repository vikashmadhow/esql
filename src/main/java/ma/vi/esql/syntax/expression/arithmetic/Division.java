/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

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

  @SafeVarargs
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

  @Override
  public Object postTransformExec(Target target, EsqlConnection esqlCon,
                                  EsqlPath path,
                                  PMap<String, Object> parameters, Environment env) {
    Object left = expr1().exec(target, esqlCon, path.add(expr1()), parameters, env);
    Object right = expr2().exec(target, esqlCon, path.add(expr2()), parameters, env);

    if (left instanceof Number ln
     && right instanceof Number rn) {
      double divisor = rn.doubleValue();
      if (divisor == 0) {
        throw new ArithmeticException("Division by 0");
      } else {
        return ln.doubleValue() / rn.doubleValue();
      }
    } else {
      throw new ExecutionException(this, "Incompatible types for " + op()
                                       + ": left " + left + ", right: " + right);
    }
  }
}
