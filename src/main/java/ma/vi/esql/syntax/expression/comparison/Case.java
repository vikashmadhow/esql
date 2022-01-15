/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.MultipleSubExpressions;
import org.pcollections.PMap;

import java.util.Iterator;
import java.util.List;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.translation.SqlServerTranslator.ADD_IIF;
import static ma.vi.esql.translation.SqlServerTranslator.DONT_ADD_IIF;
import static ma.vi.esql.translation.Translatable.Target.JSON;

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
public class Case extends MultipleSubExpressions {
  public Case(Context context, List<Expression<?, ?>> expressions) {
    super(context, "Case", expressions);
  }

  public Case(Case other) {
    super(other);
  }

  @SafeVarargs
  public Case(Case other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Case copy() {
    return new Case(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Case copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Case(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT -> {
        StringBuilder est = new StringBuilder();
        for (Iterator<Expression<?, String>> i = expressions().iterator(); i.hasNext(); ) {
          Expression<?, String> e = i.next();
          if (est.length() > 0) {
            est.append(" : ");
          }
          if (i.hasNext()) {
            Expression<?, String> expr = i.next();
            est.append(expr.translate(target, path.add(expr), parameters)).append(" ? ")
               .append(e.translate(target, path.add(e), parameters));
          } else {
            est.append(e.translate(target, path.add(e), parameters));
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
        for (Iterator<Expression<?, String>> i = expressions().iterator(); i.hasNext(); ) {
          Expression<?, String> e = i.next();
          if (est.length() > 0) {
            est.append(" else ");
          }
          if (i.hasNext()) {
            Expression<?, String> expr = i.next();
            est.append(e.translate(target, path.add(e), parameters)).append(" if ")
               .append(expr.translate(target, path.add(expr), parameters));
          } else {
            est.append(e.translate(target, path.add(e), parameters));
          }
        }
        return est.toString();
      }

      case SQLSERVER -> {
        List<Expression<?, String>> exprs = expressions();
        if (expressions().size() == 3) {
          return "iif(" + exprs.get(1).translate(target, path.add(exprs.get(1)), parameters.plusAll(DONT_ADD_IIF)) + ", "
                        + exprs.get(0).translate(target, path.add(exprs.get(0)), parameters.plusAll(ADD_IIF)) + ", "
                        + exprs.get(2).translate(target, path.add(exprs.get(2)), parameters.plusAll(ADD_IIF)) + ')';
        } else {
          StringBuilder st = new StringBuilder("case");
          for (Iterator<Expression<?, String>> i = expressions().iterator(); i.hasNext(); ) {
            Expression<?, String> e = i.next();
            if (i.hasNext()) {
              Expression<?, String> expr = i.next();
              st.append(" when ").append(expr.translate(target, path.add(expr), parameters.plusAll(DONT_ADD_IIF)))
                .append(" then ").append(e.translate(target, path.add(e), parameters.plusAll(ADD_IIF)));
            } else {
              st.append(" else ").append(e.translate(target, path.add(e), parameters.plusAll(ADD_IIF)));
            }
          }
          st.append(" end");
          return st.toString();
        }
      }

      default -> {
        StringBuilder st = new StringBuilder("case");
        for (Iterator<Expression<?, String>> i = expressions().iterator(); i.hasNext(); ) {
          Expression<?, String> e = i.next();
          if (i.hasNext()) {
            Expression<?, String> expr = i.next();
            st.append(" when ").append(expr.translate(target, path.add(expr), parameters))
              .append(" then ").append(e.translate(target, path.add(e), parameters));
          } else {
            st.append(" else ").append(e.translate(target, path.add(e), parameters));
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
    for (Iterator<Expression<?, String>> i = expressions().iterator(); i.hasNext(); ) {
      Expression<?, String> e = i.next();
      if (first) { first = false; }
      else       { st.append(" else "); }

      if (i.hasNext()) {
        e._toString(st, level, indent);
        st.append(" if ");
        i.next()._toString(st, level, indent);
      } else {
        e._toString(st, level, indent);
      }
    }
  }
}