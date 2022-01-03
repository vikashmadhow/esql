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

  @SafeVarargs
  public AddTableDefinition(AddTableDefinition other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public AddTableDefinition copy() {
    return new AddTableDefinition(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public AddTableDefinition copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new AddTableDefinition(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "add " + definition().translate(target, path.add(definition()), parameters);
  }

  public TableDefinition definition() {
    return child("definition");
  }
}
