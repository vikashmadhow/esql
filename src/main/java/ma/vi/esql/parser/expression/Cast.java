/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;

import java.util.Map;

/**
 * Casts an expression to a given type.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Cast extends Expression<Type, String> {
  public Cast(Context context, Expression<?, ?> expr, Type toType) {
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
  public String translate(Target target, Map<String, Object> parameters) {
    return switch (target) {
      case ESQL       -> toType().translate(target, parameters) + '<' + expr().translate(target, parameters) + '>';
      case POSTGRESQL -> '(' + expr().translate(target, parameters) + ")::" + toType().translate(target, parameters);
      case JSON,
           JAVASCRIPT -> expr().translate(target, parameters);    // ignore cast for Javascript
      default         -> "cast(" + expr().translate(target, parameters) + " as " + toType().translate(target, parameters) + ')';
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

  public Expression<?, String> expr() {
    return child("expr");
  }
}
