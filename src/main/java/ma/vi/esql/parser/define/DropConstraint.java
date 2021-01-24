/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

import java.util.Map;

public class DropConstraint extends AlterTableAction {
  public DropConstraint(Context context, String constraintName) {
    super(context, T2.of("constraintName", new Esql<>(context, constraintName)));
  }

  public DropConstraint(DropConstraint other) {
    super(other);
  }

  @Override
  public DropConstraint copy() {
    if (!copying()) {
      try {
        copying(true);
        return new DropConstraint(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    return "drop constraint " + constraintName();
  }

  public String constraintName() {
    return childValue("constraintName");
  }
}
