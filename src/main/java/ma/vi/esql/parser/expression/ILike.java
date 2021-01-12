/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

import static ma.vi.esql.parser.Translatable.Target.SQLSERVER;

/**
 * The case-insensitive like operator (ilike) in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ILike extends RelationalOperator {
  public ILike(Context context,
               boolean not,
               Expression<?> expr1,
               Expression<?> expr2) {
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
  public String translate(Target target) {
    if (target == SQLSERVER) {
      /*
       * SQL Server requires collation for case-insensitive like.
       */
      String e = '(' + expr1().translate(target) + ") collate Latin1_General_CI_AS"
          + (not() ? " not" : "")
          + " like (" + expr2().translate(target) + ") collate Latin1_General_CI_AS";
      if (ancestor("where") == null && ancestor("having") == null) {
        e = "iif(" + e + ", 1, 0)";
      }
      return e;
    } else {
      return super.translate(target);
    }
  }

  public boolean not() {
    return childValue("not");
  }
}