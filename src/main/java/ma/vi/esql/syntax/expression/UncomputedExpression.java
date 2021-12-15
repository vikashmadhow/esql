/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

/**
 * A wrapped expression which is not computed but sent to the client in a form
 * (such as text) that it can be interpreted there.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UncomputedExpression extends SingleSubExpression {
  public UncomputedExpression(Context context, Expression<?, String> expr) {
    super(context, "UncomputedExpr", expr);
  }

  public UncomputedExpression(UncomputedExpression other) {
    super(other);
  }

  public UncomputedExpression(UncomputedExpression other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public UncomputedExpression copy() {
    return new UncomputedExpression(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public UncomputedExpression copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new UncomputedExpression(this, value, children);
  }

  @Override
  public Type type(EsqlPath path) {
    /*
     * Uncomputed expressions are not computed and therefore their
     * representation is their type. Thus all uncomputed expressions
     * are similar to text values.
     */
    return Types.TextType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return switch (target) {
      case JAVASCRIPT -> '`' + expr().translate(target, path.add(expr()), parameters) + '`';
      case ESQL       -> "$(" + expr().translate(target, path.add(expr()), parameters) + ')';
      default         -> '\'' + expr().translate(Target.ESQL, path.add(expr()), parameters).replace("'", "''") + '\'';
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("$(");
    expr()._toString(st, level, indent);
    st.append(')');
  }
}
