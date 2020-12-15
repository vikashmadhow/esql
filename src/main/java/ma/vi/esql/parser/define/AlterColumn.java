/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

public class AlterColumn extends AlterTableAction {
  public AlterColumn(Context context, String columnName, AlterColumnDefinition definition) {
    super(context,
        T2.of("columnName", new Esql<>(context, columnName)),
        T2.of("definition", definition));
  }

  public AlterColumn(AlterColumn other) {
    super(other);
  }

  @Override
  public AlterColumn copy() {
    if (!copying()) {
      try {
        copying(true);
        return new AlterColumn(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    return "alter column " + columnName() + ' ' + definition().translate(target);
  }

  public String columnName() {
    return childValue("columnName");
  }

  public AlterColumnDefinition definition() {
    return child("definition");
  }
}
