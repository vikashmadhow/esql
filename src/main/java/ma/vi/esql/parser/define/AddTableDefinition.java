/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;

public class AddTableDefinition extends AlterTableAction {
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
  public String translate(Target target) {
    return "add " + definition().translate(target);
  }

  public TableDefinition definition() {
    return child("definition");
  }
}
