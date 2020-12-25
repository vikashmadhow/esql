/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Join;

/**
 * Abstract parent of cross-products and joins table expressions. Each of these
 * table expressions compose two table expressions, which are accessible through
 * {@link #left()} and {@link #right()}.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractJoinTableExpr extends TableExpr {
  public AbstractJoinTableExpr(Context context, String joinType, TableExpr left, TableExpr right) {
    super(context, joinType, T2.of("left", left), T2.of("right", right));
  }

  public AbstractJoinTableExpr(AbstractJoinTableExpr other) {
    super(other);
  }

  @Override
  public abstract AbstractJoinTableExpr copy();

  @Override
  public Join type() {
    if (type == null) {
      type = new Join(left().type(), right().type());
    }
    return type;
  }

  @Override
  public TableExpr forAlias(String alias) {
    TableExpr table = left().forAlias(alias);
    return table != null ? table : right().forAlias(alias);
  }

  public TableExpr left() {
    return child("left");
  }

  public TableExpr right() {
    return child("right");
  }

  private volatile Join type;
}