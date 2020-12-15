/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.base.tuple.T2;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Cast extends Expression<Type> {
  public Cast(Context context, Expression<?> expr, Type toType) {
    super(context, toType, T2.of("expr", expr));
  }

  public Cast(Cast other) {
    super(other);
  }

  @Override
  public Cast copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Cast(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return toType();
  }

  @Override
  public String translate(Target target) {
    return switch (target) {
      case ESQL       -> toType().translate(target) + '<' + expr().translate(target) + '>';
      case POSTGRESQL -> expr().translate(target) + "::" + toType().translate(target);
      case JSON,
          JAVASCRIPT  -> expr().translate(target);    // ignore cast for Javascript
      default         -> "cast(" + expr().translate(target) + " as " + toType().translate(target) + ')';
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(toType().name()).append('<');
    expr()._toString(st, level, indent);
    st.append('>');
  }

  public Type toType() {
    return value;
  }

  public Expression<?> expr() {
    return child("expr");
  }
}
