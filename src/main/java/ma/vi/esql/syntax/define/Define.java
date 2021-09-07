/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.Statement;
import ma.vi.esql.syntax.expression.Expression;

import java.util.List;

/**
 * A Define statement alters the structure of objects in the database
 * (e.g. create and alter tables statements).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class Define<V> extends Expression<V, String> implements Statement {
  Define(Context context,
         V value,
         T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  Define(Context context,
         V value,
         List<? extends Esql<?, ?>> children) {
    super(context, value, children);
  }

  public Define(Define<V> other) {
    super(other);
  }

  public Define(Define<V> other, V value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract Define<V> copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract Define<V> copy(V value, T2<String, ? extends Esql<?, ?>>... children);
}

