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
import ma.vi.esql.syntax.expression.SingleSubExpression;
import org.pcollections.PMap;

/**
 * Arithmetic negation (-) in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Negation extends SingleSubExpression {
  public Negation(Context context, Expression<?, String> negated) {
    super(context, "Negation", negated);
  }

  public Negation(Negation other) {
    super(other);
  }

  @SafeVarargs
  public Negation(Negation other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Negation copy() {
    return new Negation(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Negation copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Negation(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return '-' + expr().translate(target, esqlCon, path.add(expr()), parameters, env);
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('-');
    expr()._toString(st, level, indent);
  }

  @Override
  public Object postTransformExec(Target target, EsqlConnection esqlCon,
                                  EsqlPath path,
                                  Environment env) {
    Object expr = expr().exec(target, esqlCon, path.add(expr()), env);
    if (expr instanceof Number num) {
      if (Numbers.isReal(num)) {
        return -1 * num.doubleValue();
      } else {
        return -1 * num.longValue();
      }
    } else {
      throw new ExecutionException(this, "Incompatible types for negation: " + expr);
    }
  }
}
