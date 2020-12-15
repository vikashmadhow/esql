/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

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