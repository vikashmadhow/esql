/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;
import static ma.vi.esql.translator.SqlServerTranslator.requireIif;

/**
 * A range expression bounds an expression between two expression
 * with relational operators. Example: e1 &lt; e &lt; e2, ...
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Range extends Expression<Expression<?, String>, String> {
  public Range(Context context,
               Expression<?, String> leftExpression,
               String leftCompare,
               Expression<?, String> compareExpression,
               String rightCompare,
               Expression<?, String> rightExpression) {
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
  protected String trans(Target target, Map<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        String e = '(' + leftExpression().translate(target, parameters) + ' '
                 + leftCompare() + ' ' + compareExpression().translate(target, parameters) + " && "
                 + compareExpression().translate(target, parameters) + ' ' + rightCompare() + ' '
                 + rightExpression().translate(target, parameters) + ')';
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
      }
      default -> {
        boolean sqlServerBool = target == Target.SQLSERVER && requireIif(this, parameters);
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

  public Expression<?, String> leftExpression() {
    return child("leftExpression");
  }

  public String leftCompare() {
    return childValue("leftCompare");
  }

  public Expression<?, String> compareExpression() {
    return value;
  }

  public String rightCompare() {
    return childValue("rightCompare");
  }

  public Expression<?, String> rightExpression() {
    return child("rightExpression");
  }
}
