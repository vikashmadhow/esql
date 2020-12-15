/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

import java.util.List;

public abstract class BaseLiteral<V> extends Literal<V> {
  public BaseLiteral(Context context, V value) {
    super(context, value);
  }

  public BaseLiteral(Context context,
                     V value,
                     T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public BaseLiteral(Context context, V value, Esql<?, ?>[] children) {
    super(context, value, children);
  }

  public BaseLiteral(Context context, V value, List<? extends Esql<?, ?>> children) {
    super(context, value, children);
  }

  public BaseLiteral(BaseLiteral<V> other) {
    super(other);
  }

  @Override
  public abstract BaseLiteral<V> copy();

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(value);
  }

  @Override
  public Object value(Target target) {
    return value;
  }
}