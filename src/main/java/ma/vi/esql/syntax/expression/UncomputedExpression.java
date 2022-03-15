/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.literal.BaseLiteral;
import org.pcollections.PMap;

/**
 * A wrapped expression which is not computed but sent to the client in a form
 * (such as text) that it can be interpreted there.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UncomputedExpression extends BaseLiteral<String> {
  public UncomputedExpression(Context context, Expression<?, ?> expr) {
    super(context, "UncomputedExpr", T2.of("expr", expr));
  }

  public UncomputedExpression(UncomputedExpression other) {
    super(other);
  }

  @SafeVarargs
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
  public Type computeType(EsqlPath path) {
    /*
     * Uncomputed expressions are not computed and therefore their representation
     * is their type. Thus all uncomputed expressions are similar to text values.
     */
    return Types.TextType;
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return switch (target) {
      case JAVASCRIPT -> "`$(" + expr().translate(target, esqlCon, path.add(expr()), parameters, env) + ")`";
      case ESQL       ->  "$(" + expr().translate(target, esqlCon, path.add(expr()), parameters, env) + ')';
      default         -> "'$(" + expr().translate(Target.ESQL, esqlCon, path.add(expr()), parameters, env)
                                       .toString().replace("'", "''") + ")'";
    };
  }

  @Override
  public String exec(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    /*
     * returns the string unescaped and without surrounding quotes
     */
    return trans(Target.ESQL, esqlCon, path, null, env);
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("$(");
    expr()._toString(st, level, indent);
    st.append(')');
  }

  public Expression<?, ?> expr() {
    return child("expr");
  }
}
