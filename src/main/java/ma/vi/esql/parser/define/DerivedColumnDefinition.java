/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.expression.Expression;

/**
 * The definition of a derived column in a create table statement.
 *
 * @author vikash.madhow@gmail.com
 */
public class DerivedColumnDefinition extends ColumnDefinition {
  public DerivedColumnDefinition(Context context,
                                 String name,
                                 Expression<?> expression,
                                 Metadata metadata) {
    super(context, name, null, false, expression, metadata);
  }

  public DerivedColumnDefinition(DerivedColumnDefinition other) {
    super(other);
  }

  @Override
  public DerivedColumnDefinition copy() {
    if (!copying()) {
      try {
        copying(true);
        return new DerivedColumnDefinition(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    if (target == Target.ESQL) {
      StringBuilder st = new StringBuilder("derived \"" + name() + "\" " + expression().translate(target));
      addMetadata(st, target);
      return st.toString();
    }
    // derived expressions don't produce SQL statements directly;
    // they are just inserted into the table definitions of the core schema.
    return null;
  }
}
