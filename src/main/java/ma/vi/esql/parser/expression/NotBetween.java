/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import static ma.vi.esql.parser.Translatable.Target.JSON;
import static ma.vi.base.string.Escape.escapeJsonString;

public class NotBetween extends Between {
  public NotBetween(Context context, Expression<?> compare, Expression<?> from, Expression<?> to) {
    super(context, compare, from, to);
  }

  public NotBetween(NotBetween other) {
    super(other);
  }

  @Override
  public NotBetween copy() {
    if (!copying()) {
      try {
        copying(true);
        return new NotBetween(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    switch (target) {
      case JSON:
      case JAVASCRIPT:
        String e = '(' +
            compare().translate(target) + " < " + from().translate(target) + " || " +
            compare().translate(target) + " > " + to().translate(target) +
            ')';
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return compare().translate(target) + " not between " +
            from().translate(target) + " and " +
            to().translate(target);
    }
  }
}