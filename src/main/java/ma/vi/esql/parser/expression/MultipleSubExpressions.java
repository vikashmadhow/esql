/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import java.util.List;

abstract class MultipleSubExpressions<V> extends Expression<V> {
  public MultipleSubExpressions(Context context, V value, List<Expression<?>> expressions) {
    super(context, value, expressions);
  }

  public MultipleSubExpressions(MultipleSubExpressions<V> other) {
    super(other);
  }

  @Override
  public abstract MultipleSubExpressions<V> copy();

  public List<Expression<?>> expressions() {
    return childrenList();
  }
}