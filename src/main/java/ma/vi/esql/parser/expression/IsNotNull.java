/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import static ma.vi.esql.parser.Translatable.Target.JSON;
import static ma.vi.base.string.Escape.escapeJsonString;

public class IsNotNull extends SingleSubExpression {
  public IsNotNull(Context context, Expression expr) {
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
}