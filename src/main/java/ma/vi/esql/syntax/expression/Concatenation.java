/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.List;
import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * Concatenation operation in ESQL (||).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Concatenation extends MultipleSubExpressions {
  public Concatenation(Context context, List<Expression<?, ?>> expressions) {
    super(context, "Concat", expressions);
  }

  public Concatenation(Concatenation other) {
    super(other);
  }

  public Concatenation(Concatenation other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Concatenation copy() {
    return new Concatenation(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Concatenation copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Concatenation(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        StringBuilder st = new StringBuilder();
        for (Expression<?, String> e: expressions()) {
          st.append(st.length() == 0 ? "" : " + ")
            .append('(')
            .append(e.translate(target, path.add(e), parameters))
            .append(" || '')");
        }
        String translation = "(" + st.toString() + ")";
        if (target == JSON) {
          translation = '"' + escapeJsonString(translation) + '"';
        }
        return translation;
      }
      case SQLSERVER -> {
        StringBuilder st = new StringBuilder();
        for (Expression<?, String> e: expressions()) {
          st.append(st.length() == 0 ? "" : " + ")
            .append(e.translate(target, path.add(e), parameters));
        }
        return st.toString();
      }
      default -> {
        StringBuilder st = new StringBuilder();
        for (Expression<?, String> e: expressions()) {
          st.append(st.length() == 0 ? "" : " || ")
            .append(e.translate(target, path.add(e), parameters));
        }
        return st.toString();
      }
    }
  }
}
