/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.type.Type;

/**
 * Renames a table. This only renames the last part of the qualified
 * name of the table as the remaining part is the schema and cannot
 * be renamed (without moving schema objects, etc.)
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class RenameTable extends AlterTableAction {
  public RenameTable(Context context, String toName) {
    super(context, T2.of("toName", new Esql<>(context, toName)));
  }

  public RenameTable(RenameTable other) {
    super(other);
  }

  @Override
  public RenameTable copy() {
    if (!copying()) {
      try {
        copying(true);
        return new RenameTable(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    return "rename to " + Type.dbName(toName(), target);
  }

  public String toName() {
    return childValue("toName");
  }
}
