/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

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

  @SafeVarargs
  public RenameTable(RenameTable other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public RenameTable copy() {
    return new RenameTable(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public RenameTable copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new RenameTable(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "rename to " + Type.dbTableName(toName(), target);
  }

  public String toName() {
    return childValue("toName");
  }
}
