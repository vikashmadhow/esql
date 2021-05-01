/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Types;

import java.util.List;
import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * Returns first non-null expressions among its arguments.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Coalesce extends MultipleSubExpressions<String> {
  public Coalesce(Context context, List<Expression<?, ?>> expressions) {
    super(context, "coalesce", expressions);
  }

  public Coalesce(Coalesce other) {
    super(other);
  }

  @Override
  public Coalesce copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Coalesce(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        StringBuilder st = new StringBuilder();
        for (Expression<?, String> e: expressions()) {
          if (st.length() > 0) {
            st.append(" || ");
          }
          String t = e.translate(target, parameters);
          st.append("((" + t + ") || (" + t + ") === 0 || (" + t + ") === '' ? " + t + " : null)");
        }
        String translation = "(" + st.toString() + ")";
        if (target == JSON) {
          translation = '"' + escapeJsonString(translation) + '"';
        }
        return translation;
      }

      case ESQL -> {
        StringBuilder st = new StringBuilder();
        for (Expression<?, String> e: expressions()) {
          st.append(st.length() == 0 ? "" : "?").append(e.translate(target, parameters));
        }
        return st.toString();
      }

      default -> {
        boolean sqlServerBool = target == Target.SQLSERVER
                             && type() == Types.BoolType
                             && (ancestor("on") != null || ancestor("where") != null || ancestor("having") != null)
                             && (parent == null || parent.ancestor(Coalesce.class) == null);
        StringBuilder st = new StringBuilder();
        if (sqlServerBool) {
          st.append('(');
        }
        st.append("coalesce(");
        boolean first = true;
        for (Expression<?, String> e: expressions()) {
          if (first) { first = false;   }
          else       { st.append(", "); }
          st.append(e.translate(target, parameters));
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
