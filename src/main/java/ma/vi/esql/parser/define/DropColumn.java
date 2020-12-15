/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

public class DropColumn extends AlterTableAction {
  public DropColumn(Context context, String columnName) {
    super(context, T2.of("columnName", new Esql<>(context, columnName)));
  }

  public DropColumn(DropColumn other) {
    super(other);
  }

  @Override
  public DropColumn copy() {
    if (!copying()) {
      try {
        copying(true);
        return new DropColumn(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    return "drop column " + columnName();
  }

  public String columnName() {
    return childValue("columnName");
  }
}
