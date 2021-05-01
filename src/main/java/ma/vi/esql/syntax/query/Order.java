/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;

import java.util.Map;

/**
 * Order by clause in ESQL.
 *
 * @author vikash.madhow@gmail.com
 */
public class Order extends Esql<Expression<?, String>, String> {
  public Order(Context context, Expression<?, String> expression, String dir) {
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
  protected String trans(Target target, Map<String, Object> parameters) {
    String dir = dir();
    return order().translate(target, parameters) + (dir == null ? "" : ' ' + dir);
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    order()._toString(st, level, indent);
    if (dir() != null) {
      st.append(' ').append(dir());
    }
  }

  public Expression<?, String> order() {
    return value;
  }

  public String dir() {
    return childValue("dir");
  }
}