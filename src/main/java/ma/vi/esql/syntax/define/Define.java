/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.expression.Expression;

import java.util.List;

/**
 * A Define statement alters the structure of objects in the database
 * (e.g. create and alter tables statements).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class Define extends Expression<String, String> {
  @SafeVarargs
  Define(Context context,
         String value,
         T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  Define(Context context,
         String value,
         List<? extends Esql<?, ?>> children) {
    super(context, value, children);
  }

  public Define(Define other) {
    super(other);
  }

  @SafeVarargs
  public Define(Define other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract Define copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract Define copy(String value, T2<String, ? extends Esql<?, ?>>... children);
}

