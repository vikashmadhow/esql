/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.type.Type;
import ma.vi.base.tuple.T2;
import ma.vi.esql.type.Types;

import static ma.vi.esql.parser.Translatable.Target.JSON;
import static ma.vi.base.string.Escape.escapeJsonString;

/**
 * e1 &lt; e &lt; e2, ...
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Range extends Expression<Expression<?>> {
  public Range(Context context,
               Expression<?> leftExpression, String leftCompare,
               Expression<?> compareExpression, String rightCompare,
               Expression<?> rightExpression) {
    super(context, compareExpression,
        T2.of("leftExpression", leftExpression),
        T2.of("leftCompare", new Esql<>(context, leftCompare)),
        T2.of("rightCompare", new Esql<>(context, rightCompare)),
        T2.of("rightExpression", rightExpression));
  }

  public Range(Range other) {
    super(other);
  }

  @Override
  public Range copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Range(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.BoolType;
  }

  @Override
  public String translate(Target target) {
    switch (target) {
      case JSON:
      case JAVASCRIPT:
        String e = '(' + leftExpression().translate(target) + ' ' + leftCompare() + ' ' +
            compareExpression().translate(target) + " && " +
            compareExpression().translate(target) + ' ' + rightCompare() + ' ' +
            rightExpression().translate(target) +
            ')';
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return '(' +
            leftExpression().translate(target) + ' ' + leftCompare() + ' ' +
            compareExpression().translate(target) + " and " +
            compareExpression().translate(target) + ' ' + rightCompare() + ' ' +
            rightExpression().translate(target) +
            ')';
    }
  }

  public Expression<?> leftExpression() {
    return child("leftExpression");
  }

  public String leftCompare() {
    return childValue("leftCompare");
  }

  public Expression<?> compareExpression() {
    return value;
  }

  public String rightCompare() {
    return childValue("rightCompare");
  }

  public Expression<?> rightExpression() {
    return child("rightExpression");
  }
}
