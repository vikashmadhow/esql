/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.SingleSubExpression;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * The is null operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class IsNull extends SingleSubExpression {
  public IsNull(Context context,
                boolean not,
                Expression<?, String> expr) {
    super(context, expr);
    child("not", new Esql<>(context, not));
  }

  public IsNull(IsNull other) {
    super(other);
  }

  @Override
  public IsNull copy() {
    if (!copying()) {
      try {
        copying(true);
        return new IsNull(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    switch (target) {
      case JSON:
      case JAVASCRIPT:
        String e = expr().translate(target, parameters) + (not() ? " !== null" : " === null");
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
      default:
        return expr().translate(target, parameters) + " is" + (not() ? " not" : "") + " null";
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr()._toString(st, level, indent);
    st.append(" is");
    if (not()) {
      st.append(" not");
    }
    st.append(" null");
  }

  public boolean not() {
    return childValue("not");
  }
}