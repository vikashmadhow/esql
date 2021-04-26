/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;

import java.util.Map;

import static ma.vi.esql.syntax.Translatable.Target.SQLSERVER;
import static ma.vi.esql.translator.SqlServerTranslator.requireIif;

/**
 * The case-insensitive like operator (ilike) in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ILike extends RelationalOperator {
  public ILike(Context context,
               boolean not,
               Expression<?, String> expr1,
               Expression<?, String> expr2) {
    super(context, "ilike", expr1, expr2);
    child("not", new Esql<>(context, not));
  }

  public ILike(ILike other) {
    super(other);
  }

  @Override
  public ILike copy() {
    if (!copying()) {
      try {
        copying(true);
        return new ILike(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    if (target == SQLSERVER) {
      /*
       * SQL Server requires collation for case-insensitive like.
       */
      String e = '(' + expr1().translate(target, parameters) + ") collate Latin1_General_CI_AS"
          + (not() ? " not" : "")
          + " like (" + expr2().translate(target, parameters) + ") collate Latin1_General_CI_AS";
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