/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.Objects;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.translation.Translatable.Target.JSON;

/**
 * The equality operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Equality extends ComparisonOperator {
  public Equality(Context context, Expression<?, ?> expr1, Expression<?, ?> expr2) {
    super(context, "=", expr1, expr2);
  }

  public Equality(Equality other) {
    super(other);
  }

  @SafeVarargs
  public Equality(Equality other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Equality copy() {
    return new Equality(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Equality copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Equality(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    switch (target) {
      case JSON:
      case JAVASCRIPT:
        String e = expr1().translate(target, null, path.add(expr1()), parameters, null) + " === " + expr2().translate(target,
                                                                                                                      null,
                                                                                                                      path.add(expr2()), parameters,
                                                                                                                      null);
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return super.trans(target, esqlCon, path, parameters, env);
    }
  }

  @Override
  public Object postTransformExec(Target target, EsqlConnection esqlCon,
                                  EsqlPath path,
                                  Environment env) {
    Object left = expr1().exec(target, esqlCon, path.add(expr1()), env);
    Object right = expr2().exec(target, esqlCon, path.add(expr2()), env);
    return Objects.equals(left, right);
  }
}
