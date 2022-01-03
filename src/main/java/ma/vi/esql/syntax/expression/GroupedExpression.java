/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

/**
 * Groups an expression by surrounding it with parenthesis.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class GroupedExpression extends SingleSubExpression {
  public GroupedExpression(Context context, Expression<?, String> expr) {
    super(context, "GroupedExpr", expr);
  }

  public GroupedExpression(GroupedExpression other) {
    super(other);
  }

  @SafeVarargs
  public GroupedExpression(GroupedExpression other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public GroupedExpression copy() {
    return new GroupedExpression(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public GroupedExpression copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new GroupedExpression(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "(" + String.valueOf(expr().translate(target, path.add(expr()), parameters)) + ")";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('(');
    expr()._toString(st, level, indent);
    st.append(')');
  }
}
