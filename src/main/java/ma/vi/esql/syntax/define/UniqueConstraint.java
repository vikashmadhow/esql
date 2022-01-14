/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

import java.util.HashSet;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.MARIADB;
import static ma.vi.esql.translation.Translatable.Target.MYSQL;

/**
 * A unique constraint over one or more columns of a table.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UniqueConstraint extends ConstraintDefinition {
  public UniqueConstraint(Context context,
                          String name,
                          String table,
                          List<String> columns) {
    super(context,
          name != null ? name : defaultConstraintName("uq_", columns),
          table,
          columns);
  }

  public UniqueConstraint(UniqueConstraint other) {
    super(other);
  }

  @SafeVarargs
  public UniqueConstraint(UniqueConstraint other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public UniqueConstraint copy() {
    return new UniqueConstraint(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public UniqueConstraint copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new UniqueConstraint(this, value, children);
  }

  public boolean sameAs(ConstraintDefinition def) {
    if (def instanceof UniqueConstraint c) {
      return new HashSet<>(columns()).equals(new HashSet<>(c.columns()));
    }
    return false;
  }

  @Override
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    String name = name();
    if (name.length() >= 64 && (target == MARIADB || target == MYSQL)) {
      /*
       * Identifiers in MySQL and MariaDB are limited to 64 characters.
       */
      name = name.substring(name.length() - 64);
    }
    return "constraint "
        + '"' + name + '"'
        + " unique(" + quotedColumnsList(columns()) + ')';
  }
}
