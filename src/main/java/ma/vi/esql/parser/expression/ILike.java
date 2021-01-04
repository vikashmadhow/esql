/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import static ma.vi.esql.parser.Translatable.Target.SQLSERVER;

/**
 * The case-insensitive like operator (ilike) in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ILike extends RelationalOperator {
  public ILike(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "ilike", expr1, expr2);
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
       * SQL Server does not support ilike, hack it.
       */
      return "lower(" + expr1().translate(target) + ") like lower(" + expr2().translate(target) + ')';
    } else {
      return super.translate(target);
    }
  }
}