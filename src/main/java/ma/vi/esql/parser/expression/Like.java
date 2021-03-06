/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

import java.util.Map;

import static ma.vi.esql.parser.Translatable.Target.SQLSERVER;
import static ma.vi.esql.translator.SqlServerTranslator.requireIif;

/**
 * Like operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Like extends RelationalOperator {
  public Like(Context context,
              boolean not,
              Expression<?> expr1,
              Expression<?> expr2) {
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
  public String translate(Target target, Map<String, Object> parameters) {
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
      return super.translate(target, parameters);
    }
  }

  public boolean not() {
    return childValue("not");
  }
}