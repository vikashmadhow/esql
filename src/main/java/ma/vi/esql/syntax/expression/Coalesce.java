/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.List;
import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * Returns first non-null expressions among its arguments.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Coalesce extends MultipleSubExpressions {
  public Coalesce(Context context, List<Expression<?, ?>> expressions) {
    super(context, "Coalesce", expressions);
  }

  public Coalesce(Coalesce other) {
    super(other);
  }

  @SafeVarargs
  public Coalesce(Coalesce other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Coalesce copy() {
    return new Coalesce(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Coalesce copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Coalesce(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        StringBuilder st = new StringBuilder();
        for (Expression<?, String> e: expressions()) {
          if (st.length() > 0) {
            st.append(" || ");
          }
          String t = e.translate(target, path.add(e), parameters);
          st.append("((" + t + ") || (" + t + ") === 0 || (" + t + ") === '' ? " + t + " : null)");
        }
        String translation = "(" + st + ")";
        if (target == JSON) {
          translation = '"' + escapeJsonString(translation) + '"';
        }
        return translation;
      }

      case ESQL -> {
        StringBuilder st = new StringBuilder();
        for (Expression<?, String> e: expressions()) {
          st.append(st.length() == 0 ? "" : "?").append(e.translate(target, path.add(e), parameters));
        }
        return st.toString();
      }

      default -> {
        boolean sqlServerBool = target == Target.SQLSERVER
                             && type(path.add(this)) == Types.BoolType
                             && (path.ancestor("on") != null || path.ancestor("where") != null || path.ancestor("having") != null)
                             && (path.ancestor(FunctionCall.class) == null);
        StringBuilder st = new StringBuilder();
        if (sqlServerBool) {
          st.append('(');
        }
        st.append("coalesce(");
        boolean first = true;
        for (Expression<?, String> e: expressions()) {
          if (first) { first = false;   }
          else       { st.append(", "); }
          st.append(e.translate(target, path.add(e), parameters));
        }
        st.append(')');
        if (sqlServerBool) {
          st.append("=1)");
        }
        return st.toString();
      }
    }
  }
}
