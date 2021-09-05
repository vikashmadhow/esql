/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

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

  @Override
  public AlterColumn copy() {
    return new AlterColumn(this);
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
