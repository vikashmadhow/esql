/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.DefaultValue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A unique constraint over one or more columns of a table.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UniqueConstraint extends ConstraintDefinition {
  public UniqueConstraint(Context context, String name, List<String> columns) {
    super(context, name, T2.of("columns", new Esql<>(context, columns)));
  }

  public UniqueConstraint(UniqueConstraint other) {
    super(other);
  }

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
      //      /*
//       * Trim to work around an error (which I found only in HSQLDB for now)
//       * where the names are padded with some spaces.
//       */
//      // taken care of by array reading for HsqlDb
//      return c.columns()
//              .stream()
//              .map(String::trim)
//              .collect(toSet()).equals(columns().stream()
//                                                .map(String::trim)
//                                                .collect(toSet()));
      return new HashSet<>(columns()).equals(new HashSet<>(c.columns()));
    }
    return false;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "constraint "
        + '"' + (name() != null ? name() : defaultConstraintName(target, namePrefix())) + '"'
        + " unique(" + quotedColumnsList(columns()) + ')';
  }

  @Override
  protected String namePrefix() {
    return "uq_";
  }
}
