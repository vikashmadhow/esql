/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;

import java.util.Map;

/**
 * The definition of a derived column in a create table statement.
 *
 * @author vikash.madhow@gmail.com
 */
public class DerivedColumnDefinition extends ColumnDefinition {
  public DerivedColumnDefinition(Context context,
                                 String name,
                                 Expression<?, String> expression,
                                 Metadata metadata) {
    super(context, name, null, false, expression, metadata);
  }

  public DerivedColumnDefinition(DerivedColumnDefinition other) {
    super(other);
  }

  public DerivedColumnDefinition(DerivedColumnDefinition other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public DerivedColumnDefinition copy() {
    return new DerivedColumnDefinition(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public DerivedColumnDefinition copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new DerivedColumnDefinition(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    if (target == Target.ESQL) {
      StringBuilder st = new StringBuilder("derived \"" + name() + "\" " + expression().translate(target, path.add(expression()), parameters));
      addMetadata(st, target);
      return st.toString();
    }
    /*
     * Derived expressions don't produce SQL statements directly; they are just
     * inserted into the table definitions of the core schema.
     */
    return null;
  }
}
