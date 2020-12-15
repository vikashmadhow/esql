/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

public class Negation extends SingleSubExpression {
  public Negation(Context context, Expression negated) {
    super(context, negated);
  }

  public Negation(Negation other) {
    super(other);
  }

  @Override
  public Negation copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Negation(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    return '-' + expr().translate(target);
  }
}
