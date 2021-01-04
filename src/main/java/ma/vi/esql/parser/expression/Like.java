/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

/**
 * Like operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Like extends RelationalOperator {
  public Like(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, "like", expr1, expr2);
  }

  public Like(Like other) {
    super(other);
  }

  @Override
  public Like copy() {
    return new Like(this);
  }
}