/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;

/**
 * Abstract parent of ESQL expressions taking exactly two arguments.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
abstract class DoubleSubExpressions<V> extends Expression<V> {
  public DoubleSubExpressions(Context context, V value, Expression<?> expr1, Expression<?> expr2) {
    super(context, value, T2.of("expr1", expr1), T2.of("expr2", expr2));
  }

  public DoubleSubExpressions(DoubleSubExpressions<V> other) {
    super(other);
  }

  @Override
  public abstract DoubleSubExpressions<V> copy();

  @Override
  public Type type() {
    return expr1().type();
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr1()._toString(st, level, indent);
    st.append(' ').append(value).append(' ');
    expr2()._toString(st, level, indent);
  }

  public Expression<?> expr1() {
    return child("expr1");
  }

  public Expression<?> expr2() {
    return child("expr2");
  }
}
