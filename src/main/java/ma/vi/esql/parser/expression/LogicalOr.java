/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import static ma.vi.esql.parser.Translatable.Target.JSON;
import static ma.vi.base.string.Escape.escapeJsonString;

public class LogicalOr extends RelationalOperator {
  public LogicalOr(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "or", expr1, expr2);
  }

  public LogicalOr(LogicalOr other) {
    super(other);
  }

  @Override
  public LogicalOr copy() {
    if (!copying()) {
      try {
        copying(true);
        return new LogicalOr(this);
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
        String e = expr1().translate(target) + " || " + expr2().translate(target);
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return super.translate(target);
    }
  }
}
