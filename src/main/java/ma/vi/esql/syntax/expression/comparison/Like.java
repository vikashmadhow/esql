/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.expression.Expression;

import java.util.Map;

import static ma.vi.esql.syntax.Translatable.Target.SQLSERVER;
import static ma.vi.esql.translator.SqlServerTranslator.requireIif;

/**
 * Like operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Like extends ComparisonOperator {
  public Like(Context context,
              boolean not,
              Expression<?, String> expr1,
              Expression<?, String> expr2) {
    super(context, "like", expr1, expr2);
    child("not", new Esql<>(context, not));
  }

  public Like(Like other) {
    super(other);
  }

  @Override
  public Like copy() {
    return new Like(this);
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    if (target == SQLSERVER) {
      /*
       * SQL Server requires collation for case-sensitive like.
       */
      String e = '(' + expr1().translate(target, parameters) + ") collate Latin1_General_CS_AS"
               + (not() ? " not" : "")
               + " like (" + expr2().translate(target, parameters) + ") collate Latin1_General_CS_AS";
      if (requireIif(this, parameters)) {
        e = "iif(" + e + ", 1, 0)";
      }
      return e;
    } else {
      return super.trans(target, parameters);
    }
  }

  public boolean not() {
    return childValue("not");
  }
}