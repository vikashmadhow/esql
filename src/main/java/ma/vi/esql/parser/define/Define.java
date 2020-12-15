/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Statement;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class Define<V> extends Statement<V, String> {
  Define(Context context, V value, T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public Define(Define<V> other) {
    super(other);
  }

  @Override
  public abstract Define<V> copy();
}

