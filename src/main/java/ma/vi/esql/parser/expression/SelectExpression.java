/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.query.Select;

/**
 * A single-column, single-row select in a column list.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SelectExpression extends Expression<Select> {
  public SelectExpression(Context context, Select select) {
    super(context, select);
  }

  public SelectExpression(SelectExpression other) {
    super(other);
  }

  @Override
  public SelectExpression copy() {
    if (!copying()) {
      try {
        copying(true);
        return new SelectExpression(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    return '(' + select().translate(target).statement + ')';
  }

  public Select select() {
    return value;
  }
}
