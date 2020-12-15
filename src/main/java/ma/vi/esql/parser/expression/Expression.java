/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.base.tuple.T2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An ESQL expression.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Expression<V> extends Esql<V, String> {
  public Expression(Context context,
                    V value,
                    T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public Expression(Context context, V value, Esql<?, ?>[] children) {
    super(context, value, children);
  }

  public Expression(Context context, V value, List<? extends Esql<?, ?>> children) {
    super(context, value, children);
  }

  public Expression(Expression<V> other) {
    super(other);
  }

  @Override
  public Expression<V> copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Expression<V>(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  /**
   * Find the list of all columns referred to in the expression and add them to the set.
   */
  public Set<String> columns() {
    return columns(this, new HashSet<>());
  }

  private Set<String> columns(Expression<?> expr, Set<String> columns) {
    if (expr instanceof ColumnRef) {
      columns.add(((ColumnRef)expr).name());
    }
    for (Esql<?, ?> child: expr.children.values()) {
      if (child instanceof Expression<?>) {
        columns((Expression<?>)child, columns);
      }
    }
    return columns;
  }

//  public void basedOn(Field basedOn) {
//    children.put("basedOn", new Esql<>(context, basedOn));
//  }
//
//  /**
//   * From transient fields (in projections) this is the derived field that this
//   * expression is a derived expression of. This is kept here so that it can be
//   * then used when creating the projection type for a select that include a derived
//   * field. A derived field will normally be replaced by the expression used to
//   * calculate its value when the corresponding ColumnRef is expanded.
//   */
//  public Field basedOn() {
//    return childValue("basedOn");
//  }
}
