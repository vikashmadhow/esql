/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser;

import ma.vi.base.tuple.T2;

import java.util.List;

/**
 * The parent of all ESQL statements.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class Statement<V, R> extends Esql<V, R> {
  public Statement(Context context, V value, T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public Statement(Context context, V value, List<Esql<?, ?>> children) {
    super(context, value, children);
  }

  public Statement(Statement<V, R> other) {
    super(other);
  }

  @Override
  public abstract Statement<V, R> copy();
}
