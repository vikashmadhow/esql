/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.type.Type;
import ma.vi.base.tuple.T2;
import ma.vi.esql.type.Types;

/**
 * Order by clause in ESQL.
 *
 * @author vikash.madhow@gmail.com
 */
public class Order extends Esql<Expression<?>, String> {
  public Order(Context context, Expression<?> expression, String dir) {
    super(context, expression, T2.of("dir", new Esql<>(context, dir)));
  }

  public Order(Order other) {
    super(other);
  }

  @Override
  public Order copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Order(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.VoidType;
  }

  @Override
  public String translate(Target target) {
    String dir = dir();
    return order().translate(target) + (dir == null ? "" : ' ' + dir);
  }

  public Expression<?> order() {
    return value;
  }

  public String dir() {
    return childValue("dir");
  }
}