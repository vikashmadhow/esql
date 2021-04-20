/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Statement;

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

  public Expression(Context context, V value, Esql<?, ?>[] children) {
    super(context, value, children);
  }

  public Expression(Context context, V value, List<? extends Esql<?, ?>> children) {
    super(context, value, children);
  }

  public Expression(Expression<V, R> other) {
    super(other);
  }

  @Override
  public Expression<V, R> copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Expression<V, R>(this);
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
  public Set<String> referredColumns() {
    return referredColumns(this, new HashSet<>());
  }

  private Set<String> referredColumns(Expression<?, ?> expr, Set<String> columns) {
    if (expr instanceof ColumnRef) {
      columns.add(((ColumnRef)expr).name());
    }
    for (Esql<?, ?> child: expr.children.values()) {
      if (child instanceof Expression<?, ?>) {
        referredColumns((Expression<?, ?>)child, columns);
      }
    }
    return columns;
  }

  @Override
  public R translate(Target target, Map<String, Object> parameters) {
    if (ancestor(Statement.class) == null) {
      return (R)("select " + trans(target, parameters));
    } else {
      return trans(target, parameters);
    }
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
