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
public abstract class Alteration extends Define {
  public Alteration(Context context, T2<String, ? extends Esql<?, ?>>... children) {
    super(context, "Alteration", children);
  }

  public Alteration(Alteration other) {
    super(other);
  }

  public Alteration(Alteration other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract Alteration copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract Alteration copy(String value, T2<String, ? extends Esql<?, ?>>... children);
}