/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import static ma.vi.esql.parser.Translatable.Target.SQLSERVER;

/**
 * The negation of the case-insensitive like (not ilike) operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NotILike extends RelationalOperator {
  public NotILike(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "not ilike", expr1, expr2);
  }

  public NotILike(NotILike other) {
    super(other);
  }

  @Override
  public NotILike copy() {
    if (!copying()) {
      try {
        copying(true);
        return new NotILike(this);
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
       * SQL Server does not support ilike, hack it.
       */
      return "lower(" + expr1().translate(target) + ") not like lower(" + expr2().translate(target) + ')';
    } else {
      return super.translate(target);
    }
  }
}