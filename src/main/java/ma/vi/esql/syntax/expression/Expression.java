/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An ESQL expression.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Expression<V, R> extends Esql<V, R> {
  @SafeVarargs
  public Expression(Context context,
                    V value,
                    T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public Expression(Context context,
                    V value,
                    List<? extends Esql<?, ?>> children) {
    super(context, value, children);
  }

  public Expression(Expression<V, R> other) {
    super(other);
  }

  @SafeVarargs
  public Expression(Expression<V, R> other, V value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Expression<V, R> copy() {
    return new Expression<>(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Expression<V, R> copy(V value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Expression<>(this, value, children);
  }

  /**
   * Find the list of all columns referred to in the expression and add them to
   * the set.
   */
  public Set<String> referredColumns() {
    return referredColumns(this, new HashSet<>());
  }

  private Set<String> referredColumns(Expression<?, ?> expr, Set<String> columns) {
    if (expr instanceof ColumnRef) {
      columns.add(((ColumnRef)expr).name());
    }
    for (Esql<?, ?> child: expr.children()) {
      if (child instanceof Expression<?, ?>) {
        referredColumns((Expression<?, ?>)child, columns);
      }
    }
    return columns;
  }
}
