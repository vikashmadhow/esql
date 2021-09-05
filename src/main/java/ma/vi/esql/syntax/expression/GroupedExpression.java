/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

/**
 * Groups an expression by surrounding it with parenthesis.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class GroupedExpression extends SingleSubExpression {
  public GroupedExpression(Context context, Expression<?, String> expr) {
    super(context, expr);
  }

  public GroupedExpression(GroupedExpression other) {
    super(other);
  }

  @Override
  public GroupedExpression copy() {
    return new GroupedExpression(this);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return '(' + expr().translate(target, path.add(expr()), parameters) + ')';
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('(');
    expr()._toString(st, level, indent);
    st.append(')');
  }
}
