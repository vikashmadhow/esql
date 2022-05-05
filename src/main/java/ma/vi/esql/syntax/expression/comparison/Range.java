/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.translation.SqlServerTranslator.requireIif;
import static ma.vi.esql.translation.Translatable.Target.JSON;

/**
 * A range expression bounds an expression between two expression
 * with relational operators. Example: e1 &lt; e &lt; e2, ...
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Range extends Expression<String, String> {
  public Range(Context context,
               Expression<?, String> leftExpression,
               String leftCompare,
               Expression<?, String> compareExpression,
               String rightCompare,
               Expression<?, String> rightExpression) {
    super(context, "Range",
          T2.of("leftExpression", leftExpression),
          T2.of("leftCompare", new Esql<>(context, leftCompare)),
          T2.of("compareExpression", compareExpression),
          T2.of("rightCompare", new Esql<>(context, rightCompare)),
          T2.of("rightExpression", rightExpression));
  }

  public Range(Range other) {
    super(other);
  }

  @SafeVarargs
  public Range(Range other, String value, T2<String, ? extends Esql<?, ?>>... children) {
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
  public Range copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Range(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target target,
                         EsqlConnection esqlCon, EsqlPath path,
                         PMap<String, Object> parameters, Environment env) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        String compareEx = compareExpression().translate(target, esqlCon, path.add(compareExpression()), parameters, env);
        String e = '(' + leftExpression().translate(target, esqlCon, path.add(leftExpression()), parameters, env) + ' '
                 + leftCompare() + ' ' + compareEx + " && " + compareEx + ' ' + rightCompare() + ' '
                 + rightExpression().translate(target, esqlCon, path.add(rightExpression()), parameters, env) + ')';
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
      }
      default -> {
        boolean sqlServerBool = target == Target.SQLSERVER && requireIif(path, parameters);
        String compareEx = compareExpression().translate(target, esqlCon, path.add(compareExpression()), parameters, env);
        return (sqlServerBool ? "iif" : "") + '('
             + leftExpression().translate(target, esqlCon, path.add(leftExpression()), parameters, env) + ' ' + leftCompare() + ' '
             + compareEx + " and " + compareEx + ' ' + rightCompare() + ' '
             + rightExpression().translate(target, esqlCon, path.add(rightExpression()), parameters, env)
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
    return child("compareExpression");
  }

  public String rightCompare() {
    return childValue("rightCompare");
  }

  public Expression<?, String> rightExpression() {
    return child("rightExpression");
  }
}
