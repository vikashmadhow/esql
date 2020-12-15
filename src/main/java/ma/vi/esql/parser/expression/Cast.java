/*
 * Copyright (c) 2018 Vikash Madhow
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
    switch (target) {
      case ESQL:
      case POSTGRESQL:
        return expr().translate(target) + "::" +
            toType().translate(target);

//            case JSON:
//                if (expr() instanceof Literal) {
//                    return expr().translate(target);
//                } else {
//                    return '"' + expr().translate(target) + '"';
//                }

      case JSON:
      case JAVASCRIPT:
        return expr().translate(target);    // ignore cast for Javascript

      default:
        return "cast(" + expr().translate(target) + " as " +
            toType().translate(target) + ')';
    }
  }

  public Type toType() {
    return value;
  }

  public Expression<?> expr() {
    return child("expr");
  }
}
