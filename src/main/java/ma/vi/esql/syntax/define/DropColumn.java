/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.DefaultValue;

import java.util.Map;

/**
 * Drop a column.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DropColumn extends Alteration {
  public DropColumn(Context context, String columnName) {
    super(context, T2.of("columnName", new Esql<String, String>(context, columnName)));
  }

  public DropColumn(DropColumn other) {
    super(other);
  }

  public DropColumn(DropColumn other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public DropColumn copy() {
    return new DropColumn(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public DropColumn copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new DropColumn(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "drop column " + columnName();
  }

  public String columnName() {
    return childValue("columnName");
  }
}
