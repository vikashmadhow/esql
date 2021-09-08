/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;

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

  public Range(Range other, Expression<?, String> value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Range copy() {
    return new Range(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Range copy(Expression<?, String> value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Range(this, value, children);
  }

  @Override
  public Type type(EsqlPath path) {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target target,
                         EsqlPath path,
                         Map<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        String compareEx = compareExpression().translate(target, path.add(compareExpression()), parameters);
        String e = '(' + leftExpression().translate(target, path.add(leftExpression()), parameters) + ' '
                 + leftCompare() + ' ' + compareEx + " && " + compareEx + ' ' + rightCompare() + ' '
                 + rightExpression().translate(target, path.add(rightExpression()), parameters) + ')';
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
      }
      default -> {
        boolean sqlServerBool = target == Target.SQLSERVER && requireIif(path, parameters);
        String compareEx = compareExpression().translate(target, path.add(compareExpression()), parameters);
        return (sqlServerBool ? "iif" : "") + '('
             + leftExpression().translate(target, path.add(leftExpression()), parameters) + ' ' + leftCompare() + ' '
             + compareEx + " and " + compareEx + ' ' + rightCompare() + ' '
             + rightExpression().translate(target, path.add(rightExpression()), parameters)
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
