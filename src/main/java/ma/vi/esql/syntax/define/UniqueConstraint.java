/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;

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

  @Override
  public UniqueConstraint copy() {
    if (!copying()) {
      try {
        copying(true);
        return new UniqueConstraint(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  public boolean sameAs(ConstraintDefinition def) {
    if (def instanceof UniqueConstraint) {
      UniqueConstraint c = (UniqueConstraint)def;
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
  protected String trans(Target target, Map<String, Object> parameters) {
    return "constraint "
        + '"' + (name() != null ? name() : defaultConstraintName(target, namePrefix())) + '"'
        + " unique(" + quotedColumnsList(columns()) + ')';
  }

  @Override
  protected String namePrefix() {
    return "uq_";
  }
}
