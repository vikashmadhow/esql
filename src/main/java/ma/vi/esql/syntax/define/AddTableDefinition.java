/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

/**
 * Adds a column, constraint or other definition to a table.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AddTableDefinition extends Alteration {
  public AddTableDefinition(Context context, TableDefinition def) {
    super(context, T2.of("definition", def));
  }

  public AddTableDefinition(AddTableDefinition other) {
    super(other);
  }

  @Override
  public AddTableDefinition copy() {
    return new AddTableDefinition(this);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "add " + definition().translate(target, path.add(definition()), parameters);
  }

  public TableDefinition definition() {
    return child("definition");
  }
}
