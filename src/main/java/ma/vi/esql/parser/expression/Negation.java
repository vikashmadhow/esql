/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

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
    if (!copying()) {
      try {
        copying(true);
        return new Negation(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    return '-' + expr().translate(target, parameters);
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('-');
    expr()._toString(st, level, indent);
  }
}
