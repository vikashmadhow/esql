/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Statement;
import ma.vi.esql.parser.expression.Expression;

/**
 * A Define statement alters the structure of objects in the database
 * (e.g. create and alter tables statements).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class Define<V> extends Expression<V, String> {
  Define(Context context, V value, T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public Define(Define<V> other) {
    super(other);
  }

  @Override
  public abstract Define<V> copy();
}

