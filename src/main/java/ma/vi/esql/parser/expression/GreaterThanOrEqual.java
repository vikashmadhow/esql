/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

public class GreaterThanOrEqual extends RelationalOperator {
  public GreaterThanOrEqual(Context context, Expression<?> expr1, Expression<?> expr2) {
    super(context, ">=", expr1, expr2);
  }

  public GreaterThanOrEqual(GreaterThanOrEqual other) {
    super(other);
  }

  @Override
  public GreaterThanOrEqual copy() {
    if (!copying()) {
      try {
        copying(true);
        return new GreaterThanOrEqual(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }
}
