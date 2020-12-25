/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import java.util.Iterator;
import java.util.List;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

/**
 * Ternary condition in the form:
 *
 *   <pre>{expression when true} if {condition} else {expression when false}</pre>
 *
 * similar to python ternary condition. This is converted to a case expression in
 * SQL and the ternary conditional operator in Javascript.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Case extends MultipleSubExpressions<String> {
  public Case(Context context, List<Expression<?>> expressions) {
    super(context, "case", expressions);
  }

  public Case(Case other) {
    super(other);
  }

  @Override
  public Case copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Case(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        StringBuilder est = new StringBuilder();
        for (Iterator<Expression<?>> i = expressions().iterator(); i.hasNext(); ) {
          Expression<?> e = i.next();
          if (est.length() > 0) {
            est.append(" : ");
          }
          if (i.hasNext()) {
            est.append(i.next().translate(target)).append(" ? ")
               .append(e.translate(target));
          } else {
            est.append(e.translate(target));
          }
        }
        String ex = est.toString();
        ex = '(' + ex + ')';
        if (target == JSON) {
          ex = '"' + escapeJsonString(ex) + '"';
        }
        return ex;
      }

      case ESQL -> {
        StringBuilder est = new StringBuilder();
        for (Iterator<Expression<?>> i = expressions().iterator(); i.hasNext(); ) {
          Expression<?> e = i.next();
          if (est.length() > 0) {
            est.append(" else ");
          }
          if (i.hasNext()) {
            est.append(e.translate(target)).append(" if ")
               .append(i.next().translate(target));
          } else {
            est.append(e.translate(target));
          }
        }
        return est.toString();
      }

      default -> {
        StringBuilder st = new StringBuilder("case");
        for (Iterator<Expression<?>> i = expressions().iterator(); i.hasNext(); ) {
          Expression<?> e = i.next();
          if (i.hasNext()) {
            st.append(" when ").append(i.next().translate(target))
              .append(" then ").append(e.translate(target));
          } else {
            st.append(" else ").append(e.translate(target));
          }
        }
        st.append(" end");
        return st.toString();
      }
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    boolean first = true;
    for (Iterator<Expression<?>> i = expressions().iterator(); i.hasNext(); ) {
      Expression<?> e = i.next();
      if (first) { first = false; }
      else       { st.append(" else "); }

      if (i.hasNext()) {
        i.next()._toString(st, level, indent);
        st.append(" if ");
        e._toString(st, level, indent);
      } else {
        e._toString(st, level, indent);
      }
    }
  }
}
