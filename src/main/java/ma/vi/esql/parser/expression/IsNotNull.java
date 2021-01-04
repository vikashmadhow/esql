/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

/**
 * The is not null operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class IsNotNull extends SingleSubExpression {
  public IsNotNull(Context context, Expression<?> expr) {
    super(context, expr);
  }

  public IsNotNull(IsNotNull other) {
    super(other);
  }

  @Override
  public IsNotNull copy() {
    if (!copying()) {
      try {
        copying(true);
        return new IsNotNull(this);
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
  public String translate(Target target) {
    switch (target) {
      case JSON:
      case JAVASCRIPT:
        String e = expr().translate(target) + " !== null";
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return expr().translate(target) + " is not null";
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr()._toString(st, level, indent);
    st.append(" is not null");
  }
}