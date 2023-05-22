/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.MultipleSubExpressions;
import org.pcollections.PMap;

import java.util.Iterator;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.JAVASCRIPT;

/**
 * Coalesce expression in the form:
 *
 *   <pre>e1 ? e2 ? ...</pre>
 *
 * evaluation to the first non-null expression in the list.
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
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    StringBuilder sb = new StringBuilder();
    if (target == JAVASCRIPT) {
      for (Expression<?, ?> arg: expressions()) {
        sb.append(sb.length() == 0 ? "(" : " || ")
          .append(arg.translate(target, esqlCon, path.add(arg), env));
      }
    } else {
      /*
       * All databases
       */
      for (Expression<?, ?> arg: expressions()) {
        sb.append(sb.length() == 0 ? "coalesce(" : ", ")
          .append(arg.translate(target, esqlCon, path.add(arg), env));
      }
    }
    return sb.append(')').toString();
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    for (Iterator<Expression<?, ?>> i = expressions().iterator(); i.hasNext(); ) {
      i.next()._toString(st, level, indent);
      if (i.hasNext()) st.append('?');
    }
  }

  @Override
  public Object postTransformExec(Target               target,
                                  EsqlConnection       esqlCon,
                                  EsqlPath             path,
                                  PMap<String, Object> parameters,
                                  Environment          env) {
    for (Iterator<Expression<?, ?>> i = expressions().iterator(); i.hasNext(); ) {
      Expression<?, ?> e = i.next();
      Object value = e.exec(target, esqlCon, path.add(e), parameters, env);
      if (value != null) {
        return value;
      }
    }
    return null;
  }
}
