/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Join;
import ma.vi.esql.syntax.Esql;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Abstract parent of cross-products and joins table expressions. Each of these
 * table expressions compose two table expressions, which are accessible through
 * {@link #left()} and {@link #right()}.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractJoinTableExpr extends TableExpr {
  public AbstractJoinTableExpr(Context context,
                               String joinType,
                               TableExpr left,
                               TableExpr right,
                               T2<String, ? extends Esql<?, ?>>... children) {
    super(context,
          joinType,
          Stream.concat(
            Arrays.stream(
              new T2[]{
                T2.of("left", left),
                T2.of("right", right)
              }),
            Arrays.stream(children)).toArray(T2[]::new));
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

  private transient volatile Join type;
}