/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.NegatableDoubleSubExpressions;
import org.pcollections.PMap;

import static ma.vi.esql.translation.SqlServerTranslator.requireIif;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Like operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Like extends NegatableDoubleSubExpressions<String> {
  public Like(Context context,
              boolean not,
              Expression<?, String> expr1,
              Expression<?, String> expr2) {
    super(context, "Like", not, expr1, expr2);
  }

  public Like(Like other) {
    super(other);
  }

  @SafeVarargs
  public Like(Like other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Like copy() {
    return new Like(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Like copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Like(this, value, children);
  }

  @Override
  protected String trans(Target target,
                         EsqlConnection esqlCon, EsqlPath path,
                         PMap<String, Object> parameters, Environment env) {
    if (target == SQLSERVER) {
      /*
       * SQL Server requires collation for case-sensitive like.
       */
      String e = '(' + expr1().translate(target, null, path.add(expr1()), parameters, null) + ") collate Latin1_General_CS_AS"
               + (not() ? " not" : "")
               + " like (" + expr2().translate(target, null, path.add(expr2()), parameters, null) + ") collate Latin1_General_CS_AS";
      if (requireIif(path, parameters)) {
        e = "iif(" + e + ", 1, 0)";
      }
      return e;
    } else {
      return super.trans(target, esqlCon, path, parameters, env);
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr1()._toString(st, level, indent);
    if (not()) {
      st.append(" not");
    }
    st.append(" like ");
    expr2()._toString(st, level, indent);
  }

  public boolean not() {
    return childValue("not");
  }
}