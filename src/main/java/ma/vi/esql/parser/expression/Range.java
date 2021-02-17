/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;
import static ma.vi.esql.translator.SqlServerTranslator.requireIif;

/**
 * A range expression bounds an expression between two expression
 * with relational operators. Example: e1 &lt; e &lt; e2, ...
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
  public String translate(Target target, Map<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        String e = '(' + leftExpression().translate(target, parameters) + ' '
                 + leftCompare() + ' ' + compareExpression().translate(target, parameters) + " && "
                 + compareExpression().translate(target, parameters) + ' ' + rightCompare() + ' '
                 + rightExpression().translate(target, parameters) + ')';
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
      }
      default -> {
        boolean sqlServerBool = target == Target.SQLSERVER && requireIif(this);
        return (sqlServerBool ? "iif" : "") + '('
             + leftExpression().translate(target, parameters) + ' ' + leftCompare() + ' '
             + compareExpression().translate(target, parameters) + " and "
             + compareExpression().translate(target, parameters) + ' ' + rightCompare() + ' '
             + rightExpression().translate(target, parameters)
             + (sqlServerBool ? ", 1, 0" : "") + ')';
      }
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    leftExpression()._toString(st, level, indent);
    st.append(' ').append(leftCompare()).append(' ');
    compareExpression()._toString(st, level, indent);
    st.append(' ').append(rightCompare()).append(' ');
    rightExpression()._toString(st, level, indent);
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
