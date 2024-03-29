/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define.table;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.define.Define;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * The abstract parent of table definitions (such as columns, constraints, etc.).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class TableDefinition extends Define {
  @SafeVarargs
  public TableDefinition(Context context,
                         String value,
                         T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public TableDefinition(Context context,
                         String name,
                         List<? extends Esql<?, ?>> children) {
    super(context, name, children);
  }

  public TableDefinition(TableDefinition other) {
    super(other);
  }

  @SafeVarargs
  public TableDefinition(TableDefinition other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract TableDefinition copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract TableDefinition copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  public String name() {
    return childValue("name");
  }

  protected static String quotedColumnsList(List<String> columns) {
    return columns.stream()
                  .map(f -> '"' + f + '"')
                  .collect(joining(", "));
  }
}