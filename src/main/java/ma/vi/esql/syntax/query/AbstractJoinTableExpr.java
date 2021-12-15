/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Join;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

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
    super(context, "Join",
          Stream.concat(
            Arrays.stream(
              new T2[]{
                T2.of("left", left),
                T2.of("right", right),
                T2.of("joinType", new Esql<>(context, joinType))
              }),
            Arrays.stream(children)).toArray(T2[]::new));
  }

  public AbstractJoinTableExpr(AbstractJoinTableExpr other) {
    super(other);
  }

  public AbstractJoinTableExpr(AbstractJoinTableExpr other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract AbstractJoinTableExpr copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract AbstractJoinTableExpr copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  public Join type(EsqlPath path) {
    if (type == null) {
      type = new Join(left().type(path.add(left())), right().type(path.add(right())));
    }
    return type;
  }

  @Override
  public TableExpr forAlias(String alias) {
    TableExpr table = left().forAlias(alias);
    return table != null ? table : right().forAlias(alias);
  }

  public String joinType() {
    return childValue("joinType");
  }

  public TableExpr left() {
    return child("left");
  }

  public TableExpr right() {
    return child("right");
  }

  private transient volatile Join type;
}