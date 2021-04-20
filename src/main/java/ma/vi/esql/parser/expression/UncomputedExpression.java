/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.Map;

/**
 * A wrapped expression which is not computed but sent to the client in a form
 * (such as text) that it can be interpreted there.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UncomputedExpression extends SingleSubExpression {
  public UncomputedExpression(Context context, Expression<?, String> expr) {
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
  public Type type() {
    /*
     * Uncomputed expressions are not computed and therefore their
     * representation is their type. Thus all uncomputed expressions
     * are similar to text values.
     */
    return Types.TextType;
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    return switch (target) {
      case JAVASCRIPT -> '`' + expr().translate(target, parameters) + '`';
      case ESQL       -> "$(" + expr().translate(target, parameters) + ')';
      default         -> '\'' + expr().translate(Target.ESQL, parameters).replace("'", "''") + '\'';
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("$(");
    expr()._toString(st, level, indent);
    st.append(')');
  }
}
