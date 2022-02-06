/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

/**
 * Order by clause in ESQL.
 *
 * @author vikash.madhow@gmail.com
 */
public class Order extends Esql<String, String> {
  public Order(Context context, Expression<?, String> expression, String dir) {
    super(context, "Order",
          T2.of("expr", expression),
          T2.of("dir", new Esql<>(context, dir)));
  }

  public Order(Order other) {
    super(other);
  }

  @SafeVarargs
  public Order(Order other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Order copy() {
    return new Order(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public Order copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Order(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    String dir = dir();
    return order().translate(target, esqlCon, path.add(order()), parameters, env) + (dir == null ? "" : ' ' + dir);
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    order()._toString(st, level, indent);
    if (dir() != null) {
      st.append(' ').append(dir());
    }
  }

  public Expression<?, String> order() {
    return child("expr");
  }

  public String dir() {
    return childValue("dir");
  }
}