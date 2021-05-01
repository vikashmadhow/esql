/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * Parent of binary operators in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class BinaryOperator extends DoubleSubExpressions<String> {
  public BinaryOperator(Context context,
                        String op,
                        Expression<?, ?> expr1,
                        Expression<?, ?> expr2) {
    super(context, op, expr1, expr2);
  }

  public BinaryOperator(BinaryOperator other) {
    super(other);
  }

  @Override
  public abstract BinaryOperator copy();

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    String e = expr1().translate(target, parameters) + ' ' + op() + ' ' + expr2().translate(target, parameters);
    return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
  }

  public String op() {
    return value;
  }
}
