/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

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

  @SafeVarargs
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

  @Override
  public Object postTransformExec(Target target, EsqlConnection esqlCon,
                                  EsqlPath path,
                                  PMap<String, Object> parameters, Environment env) {
    Object left  = expr1().exec(target, esqlCon, path.add(expr1()), parameters, env);
    Object right = expr2().exec(target, esqlCon, path.add(expr2()), parameters, env);

    if (left  instanceof Number ln
     && right instanceof Number rn) {
      return Numbers.isReal(ln) || Numbers.isReal(rn)
           ? ln.doubleValue() >= rn.doubleValue()
           :   ln.longValue() >= rn.longValue();

    } else if (left  instanceof String
            || right instanceof String) {
      return String.valueOf(left).compareTo(String.valueOf(right)) >= 0;

    } else if (left  instanceof Date d1
            && right instanceof Date d2) {
      return d1.equals(d2) || d1.after(d2);

    } else if (left  instanceof LocalDate d1
            && right instanceof LocalDate d2) {
      return d1.isEqual(d2) || d1.isAfter(d2);

    } else if (left  instanceof LocalTime t1
            && right instanceof LocalTime t2) {
      return t1.equals(t2) || t1.isAfter(t2);

    } else if (left  instanceof LocalDateTime d1
            && right instanceof LocalDateTime d2) {
      return d1.isEqual(d2) || d1.isAfter(d2);

    } else if (left  instanceof Boolean b1
            && right instanceof Boolean b2) {
      return b1 || b1.equals(b2);

    } else {
      throw new ExecutionException(this, "Incompatible types for " + op()
                                       + ": left " + left + ", right: " + right);
    }
  }
}
