/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

/**
 * Drop a constraint from a table.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DropConstraint extends Alteration {
  public DropConstraint(Context context, String constraintName) {
    super(context, T2.of("constraintName", new Esql<>(context, constraintName)));
  }

  public DropConstraint(DropConstraint other) {
    super(other);
  }

  public DropConstraint(DropConstraint other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public DropConstraint copy() {
    return new DropConstraint(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public DropConstraint copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new DropConstraint(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "drop constraint " + constraintName();
  }

  public String constraintName() {
    return childValue("constraintName");
  }
}
