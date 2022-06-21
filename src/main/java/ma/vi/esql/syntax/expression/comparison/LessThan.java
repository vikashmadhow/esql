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
 * Less-than operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LessThan extends ComparisonOperator {
  public LessThan(Context          context,
                  Expression<?, ?> expr1,
                  Expression<?, ?> expr2) {
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

  @Override
  public Object postTransformExec(Target target, EsqlConnection esqlCon,
                                  EsqlPath path,
                                  PMap<String, Object> parameters, Environment env) {
    Object left  = expr1().exec(target, esqlCon, path.add(expr1()), parameters, env);
    Object right = expr2().exec(target, esqlCon, path.add(expr2()), parameters, env);

    if (left  instanceof Number ln
     && right instanceof Number rn) {
      return Numbers.isReal(ln) || Numbers.isReal(rn)
           ? ln.doubleValue() < rn.doubleValue()
           :   ln.longValue() < rn.longValue();

    } else if (left  instanceof String
            || right instanceof String) {
      return String.valueOf(left).compareTo(String.valueOf(right)) < 0;

    } else if (left  instanceof Date d1
            && right instanceof Date d2) {
      return d1.before(d2);

    } else if (left  instanceof LocalDate d1
            && right instanceof LocalDate d2) {
      return d1.isBefore(d2);

    } else if (left  instanceof LocalTime t1
            && right instanceof LocalTime t2) {
      return t1.isBefore(t2);

    } else if (left  instanceof LocalDateTime d1
            && right instanceof LocalDateTime d2) {
      return d1.isBefore(d2);

    } else if (left  instanceof Boolean b1
            && right instanceof Boolean b2) {
      return !b1 && b2;

    } else {
      throw new ExecutionException(this, "Incompatible types for " + op()
                                       + ": left " + left + ", right: " + right);
    }
  }
}
