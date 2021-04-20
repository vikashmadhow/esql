/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

/**
 * The equality operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Equality extends RelationalOperator {
  public Equality(Context context, Expression<?, ?> expr1, Expression<?, ?> expr2) {
    super(context, "=", expr1, expr2);
  }

  public Equality(Equality other) {
    super(other);
  }

  @Override
  public Equality copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Equality(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    switch (target) {
      case JSON:
      case JAVASCRIPT:
        String e = expr1().translate(target, parameters) + " === " + expr2().translate(target, parameters);
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return super.trans(target, parameters);
    }
  }
}
