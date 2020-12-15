/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

public class GroupedExpression extends SingleSubExpression {
  public GroupedExpression(Context context, Expression<?> expr) {
    super(context, expr);
  }

  public GroupedExpression(GroupedExpression other) {
    super(other);
  }

  @Override
  public GroupedExpression copy() {
    if (!copying()) {
      try {
        copying(true);
        return new GroupedExpression(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    return '(' + expr().translate(target) + ')';
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('(');
    expr()._toString(st, level, indent);
    st.append(')');
  }
}
