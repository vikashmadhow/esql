/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * The exponentiation operator (^) in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Exponentiation extends ArithmeticOperator {
  public Exponentiation(Context context, Expression<?, String> expr1, Expression<?, String> expr2) {
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
  protected String trans(Translatable.Target target, Map<String, Object> parameters) {
    if (target == Translatable.Target.SQLSERVER) {
      return "POWER(" + expr1().translate(target, parameters) + ", " + expr2().translate(target, parameters) + ")";
    } else {
      String e = expr1().translate(target, parameters) + " ^ " + expr2().translate(target, parameters);
      return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
    }
  }
}
