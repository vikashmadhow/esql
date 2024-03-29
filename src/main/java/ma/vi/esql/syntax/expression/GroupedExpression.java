/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

/**
 * Groups an expression by surrounding it with parenthesis.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class GroupedExpression extends SingleSubExpression {
  public GroupedExpression(Context context, Expression<?, ?> expr) {
    super(context, "GroupedExpr", peel(expr));
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

  /**
   * Peel off unnecessary groupings.
   */
  private static Expression<?, ?> peel(Expression<?, ?> expr) {
    Expression<?, ?> in = expr;
    while (in instanceof GroupedExpression g) in = g.expr();
    return in;
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return "(" + String.valueOf(expr().translate(target, esqlCon, path.add(expr()), parameters, env)) + ")";
  }

  @Override
  public Object postTransformExec(Target target, EsqlConnection esqlCon,
                                  EsqlPath path,
                                  PMap<String, Object> parameters, Environment env) {
    return expr().exec(target, esqlCon, path.add(expr()), parameters, env);
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('(');
    expr()._toString(st, level, indent);
    st.append(')');
  }
}
