/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An ESQL expression.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Expression<V, R> extends Esql<V, R> {
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

  @Override
  public Expression<V, R> copy() {
    return new Expression<>(this);
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
