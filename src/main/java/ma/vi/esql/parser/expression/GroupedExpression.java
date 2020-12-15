/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

public class GroupedExpression extends SingleSubExpression {
  public GroupedExpression(Context context, Expression<?> expr) {
    super(context, expr);
  }

  public GroupedExpression(GroupedExpression other) {
    super(other);
  }

  @Override
  public GroupedExpression copy() {
    if (!copying()) {
      try {
        copying(true);
        return new GroupedExpression(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    return '(' + expr().translate(target) + ')';
  }
}
