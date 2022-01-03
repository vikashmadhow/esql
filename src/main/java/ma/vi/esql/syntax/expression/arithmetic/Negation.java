/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.SingleSubExpression;

import java.util.Map;

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
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return '-' + expr().translate(target, path.add(expr()), parameters);
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('-');
    expr()._toString(st, level, indent);
  }
}
