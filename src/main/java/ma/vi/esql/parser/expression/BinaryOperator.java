/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

/**
 * Parent of binary operators in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
abstract class BinaryOperator extends DoubleSubExpressions<String> {
  public BinaryOperator(Context context,
                        String op,
                        Expression<?> expr1,
                        Expression<?> expr2) {
    super(context, op, expr1, expr2);
  }

  public BinaryOperator(BinaryOperator other) {
    super(other);
  }

  @Override
  public abstract BinaryOperator copy();

  @Override
  public String translate(Target target) {
    String e = expr1().translate(target) + ' ' + op() + ' ' + expr2().translate(target);
    return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
  }

  public String op() {
    return value;
  }
}
