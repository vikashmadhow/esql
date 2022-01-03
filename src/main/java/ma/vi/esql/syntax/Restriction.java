/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

import ma.vi.esql.semantic.type.Relation;

import java.util.Objects;
import java.util.Set;

import static java.util.Collections.singleton;

/**
 * Represents a restriction on a query which can be used to
 * apply query-level security.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Restriction {
  public Restriction(Relation restrictToTable) {
    this(singleton(new By(restrictToTable, null)), false, null, null);
  }

  public Restriction(Set<By> by,
                     boolean excluded,
                     Set<String> values,
                     Set<String> operations) {
    this.by = by;
    this.excluded = excluded;
    this.values = values;
    this.operations = operations;
  }

  public record By(Relation table, String column) {
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      By by = (By)o;
      return Objects.equals(table, by.table)
          && Objects.equals(column, by.column);
    }

    @Override
    public int hashCode() {
      return Objects.hash(table, column);
    }

    @Override
    public String toString() {
      return table + (column == null ? "" : ":" + column);
    }
  }

  public final Set<By> by;
  public final boolean excluded;
  public final Set<String> values;
  public final Set<String> operations;
}
