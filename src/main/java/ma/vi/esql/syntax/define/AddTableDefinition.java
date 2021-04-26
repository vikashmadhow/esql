/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;

import java.util.Map;

public class AddTableDefinition extends Alteration {
  public AddTableDefinition(Context context, TableDefinition def) {
    super(context, T2.of("definition", def));
  }

  public AddTableDefinition(AddTableDefinition other) {
    super(other);
  }

  @Override
  public AddTableDefinition copy() {
    if (!copying()) {
      try {
        copying(true);
        return new AddTableDefinition(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    return "add " + definition().translate(target, parameters);
  }

  public TableDefinition definition() {
    return child("definition");
  }
}
