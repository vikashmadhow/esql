/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

/**
 * The inequality operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Inequality extends RelationalOperator {
  public Inequality(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "!=", expr1, expr2);
  }

  public Inequality(Inequality other) {
    super(other);
  }

  @Override
  public Inequality copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Inequality(this);
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
        String e = expr1().translate(target) + " !== " + expr2().translate(target);
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return super.translate(target);
    }
  }
}
