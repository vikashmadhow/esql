/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.esql.syntax.Context;
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
  public Negation(Context context, Expression negated) {
    super(context, negated);
  }

  public Negation(Negation other) {
    super(other);
  }

  @Override
  public Negation copy() {
    return new Negation(this);
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
