/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import static ma.vi.esql.parser.Translatable.Target.JSON;
import static ma.vi.base.string.Escape.escapeJsonString;

public class Exponentiation extends ArithmeticOperator {
  public Exponentiation(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "^", expr1, expr2);
  }

  public Exponentiation(Exponentiation other) {
    super(other);
  }

  @Override
  public Exponentiation copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Exponentiation(this);
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
      case SQLSERVER:
        return "POWER(" + expr1().translate(target) + ", " + expr2().translate(target) + ")";

      default:
        String e = expr1().translate(target) + " ^ " + expr2().translate(target);
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
    }
  }
}
