/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * The inequality operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Inequality extends ComparisonOperator {
  public Inequality(Context context, Expression<?, String> expr1, Expression<?, String> expr2) {
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
  protected String trans(Translatable.Target target, Map<String, Object> parameters) {
    switch (target) {
      case JSON:
      case JAVASCRIPT:
        String e = expr1().translate(target, parameters) + " !== " + expr2().translate(target, parameters);
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return super.trans(target, parameters);
    }
  }
}
