/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

public class NotLike extends RelationalOperator {
  public NotLike(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "not like", expr1, expr2);
  }

  public NotLike(NotLike other) {
    super(other);
  }

  @Override
  public NotLike copy() {
    if (!copying()) {
      try {
        copying(true);
        return new NotLike(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }
}