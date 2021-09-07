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
 * Alter a column (name, type, etc.)
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AlterColumn extends Alteration {
  public AlterColumn(Context context, String columnName, AlterColumnDefinition definition) {
    super(context,
          T2.of("columnName", new Esql<>(context, columnName)),
          T2.of("definition", definition));
  }

  public AlterColumn(AlterColumn other) {
    super(other);
  }

  public AlterColumn(AlterColumn other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public AlterColumn copy() {
    return new AlterColumn(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public AlterColumn copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new AlterColumn(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "alter column " + columnName() + ' ' + definition().translate(target, path.add(definition()), parameters);
  }

  public String columnName() {
    return childValue("columnName");
  }

  public AlterColumnDefinition definition() {
    return child("definition");
  }
}
