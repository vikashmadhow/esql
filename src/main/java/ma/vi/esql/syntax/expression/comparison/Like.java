/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.NegatableDoubleSubExpressions;

import java.util.Map;

import static ma.vi.esql.syntax.Translatable.Target.SQLSERVER;
import static ma.vi.esql.translator.SqlServerTranslator.requireIif;

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
    super(context, "like", not, expr1, expr2);
  }

  public Like(Like other) {
    super(other);
  }

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
                         EsqlPath path,
                         Map<String, Object> parameters) {
    if (target == SQLSERVER) {
      /*
       * SQL Server requires collation for case-sensitive like.
       */
      String e = '(' + expr1().translate(target, path.add(expr1()), parameters) + ") collate Latin1_General_CS_AS"
               + (not() ? " not" : "")
               + " like (" + expr2().translate(target, path.add(expr2()), parameters) + ") collate Latin1_General_CS_AS";
      if (requireIif(path, parameters)) {
        e = "iif(" + e + ", 1, 0)";
      }
      return e;
    } else {
      return super.trans(target, path, parameters);
    }
  }

  public boolean not() {
    return childValue("not");
  }
}