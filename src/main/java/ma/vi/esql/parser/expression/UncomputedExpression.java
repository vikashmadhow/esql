/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

/**
 * An wrapped expression which is not computed but sent in to the
 * client in a form that it can be interpreted there.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UncomputedExpression extends SingleSubExpression {
  public UncomputedExpression(Context context, Expression<?> expr) {
    super(context, expr);
  }

  public UncomputedExpression(UncomputedExpression other) {
    super(other);
  }

  @Override
  public UncomputedExpression copy() {
    if (!copying()) {
      try {
        copying(true);
        return new UncomputedExpression(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    return switch (target) {
      case JAVASCRIPT -> '`' + expr().translate(target) + '`';
      case ESQL       -> "$(" + expr().translate(target) + ')';
      default         -> '\'' + expr().translate(target).replace("'", "''") + '\'';
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("$(");
    expr()._toString(st, level, indent);
    st.append(')');
  }
}
