/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.Iterator;
import java.util.List;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.ESQL;
import static ma.vi.esql.parser.Translatable.Target.JSON;

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
  public Type type() {
    for (Iterator<Expression<?>> i = expressions().iterator(); i.hasNext(); ) {
      Expression<?> e = i.next();
      Type type = i.hasNext() ? i.next().type() : e.type();
      if (!type.equals(Types.NullType)) {
        return type;
      }
    }
    return Types.TopType;
  }

  @Override
  public String translate(Target target) {
    switch (target) {
      case JSON, JAVASCRIPT, ESQL -> {
        StringBuilder est = new StringBuilder();
        for (Iterator<Expression<?>> i = expressions().iterator(); i.hasNext(); ) {
          Expression<?> e = i.next();
          if (est.length() > 0) {
            est.append(" : ");
          }
          if (i.hasNext()) {
            est.append(e.translate(target))
               .append(target == ESQL ? " -> " : " ? ")
               .append(i.next().translate(target));
          } else {
            est.append(e.translate(target));
          }
        }
        String ex = est.toString();
        if (target != ESQL) {
          ex = '(' + ex + ')';
        }
        if (target == JSON) {
          ex = '"' + escapeJsonString(ex) + '"';
        }
        return ex;
      }
      default -> {
        StringBuilder st = new StringBuilder("case");
        for (Iterator<Expression<?>> i = expressions().iterator(); i.hasNext(); ) {
          Expression<?> e = i.next();
          if (i.hasNext()) {
            st.append(" when ").append(e.translate(target))
              .append(" then ").append(i.next().translate(target));
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
      else       { st.append(" : "); }

      if (i.hasNext()) {
        e._toString(st, level, indent);
        st.append(" -> ");
        i.next()._toString(st, level, indent);
      } else {
        e._toString(st, level, indent);
      }
    }
  }
}
