/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.type.Type;

import java.util.Map;

/**
 * Renames a table. This only renames the last part of the qualified
 * name of the table as the remaining part is the schema and cannot
 * be renamed (without moving schema objects, etc.)
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class RenameTable extends Alteration {
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
  protected String trans(Target target, Map<String, Object> parameters) {
    return "rename to " + Type.dbTableName(toName(), target);
  }

  public String toName() {
    return childValue("toName");
  }
}
