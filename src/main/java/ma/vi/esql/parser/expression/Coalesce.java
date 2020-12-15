/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

public class Coalesce extends DoubleSubExpressions<String> {
  public Coalesce(Context context, Expression<?> left, Expression<?> right) {
    super(context, "coalesce", left, right);
  }

  public Coalesce(Coalesce other) {
    super(other);
  }

  @Override
  public Coalesce copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Coalesce(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    Type type = expr1().type();
    if (!type.equals(Types.NullType)) {
      return type;
    }
    return Types.TopType;
  }

  @Override
  public String translate(Target target) {
    switch (target) {
      case JSON:
      case JAVASCRIPT:
        String e1 = expr1().translate(target);
        String translation = "((" + e1 + ") || (" + e1 + ") === 0 || (" + e1 + ") === '' ? " + e1 + " : " + expr2().translate(target) + ')';
        if (target == JSON) {
          translation = '"' + escapeJsonString(translation) + '"';
        }
        return translation;

      case ESQL:
        return expr1().translate(target) + " ? " + expr2().translate(target);

      default:
        return "coalesce(" + expr1().translate(target) + ", " + expr2().translate(target) + ')';
    }
  }
}
