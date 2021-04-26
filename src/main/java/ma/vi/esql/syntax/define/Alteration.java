/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;

/**
 * An alteration provided to an alter table statement (rename table, rename column, etc.).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class Alteration extends Define<String> {
  public Alteration(Context context, T2<String, ? extends Esql<?, ?>>... children) {
    super(context, "alter", children);
  }

  public Alteration(Alteration other) {
    super(other);
  }

  @Override
  public abstract Alteration copy();
}