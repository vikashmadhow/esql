/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import static ma.vi.esql.parser.Translatable.Target.JSON;
import static ma.vi.base.string.Escape.escapeJsonString;

public class LogicalAnd extends RelationalOperator {
  public LogicalAnd(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "and", expr1, expr2);
  }

  public LogicalAnd(LogicalAnd other) {
    super(other);
  }

  @Override
  public LogicalAnd copy() {
    if (!copying()) {
      try {
        copying(true);
        return new LogicalAnd(this);
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
        String e = expr1().translate(target) + " && " + expr2().translate(target);
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return super.translate(target);
    }
  }
}
