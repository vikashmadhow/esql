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
 * A primary key constraint on a table.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class PrimaryKeyConstraint extends ConstraintDefinition {
  public PrimaryKeyConstraint(Context context,
                              String name,
                              String table,
                              List<String> columns) {
    super(context,
          name != null ? name : defaultConstraintName("pk_", columns),
          table,
          columns);
  }

  public PrimaryKeyConstraint(PrimaryKeyConstraint other) {
    super(other);
  }

  @SafeVarargs
  public PrimaryKeyConstraint(PrimaryKeyConstraint other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public PrimaryKeyConstraint copy() {
    return new PrimaryKeyConstraint(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public PrimaryKeyConstraint copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new PrimaryKeyConstraint(this, value, children);
  }

  public boolean sameAs(ConstraintDefinition def) {
    if (def instanceof PrimaryKeyConstraint c) {
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
         + " primary key(" + quotedColumnsList(columns()) + ')';
  }
}
