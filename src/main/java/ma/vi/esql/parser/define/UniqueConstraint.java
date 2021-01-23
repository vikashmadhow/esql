/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

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
      /*
       * Trim to work around an error (which I found only in HSQLDB for not)
       * where the names are padded with some spaces.
       */
      return c.columns()
              .stream()
              .map(String::trim)
              .collect(toSet()).equals(columns().stream()
                                                .map(String::trim)
                                                .collect(toSet()));
    }
    return false;
  }

  @Override
  public String translate(Target target) {
    return "constraint "
        + '"' + (name() != null ? name() : defaultConstraintName()) + '"'
        + " unique(" + quotedColumnsList(columns()) + ')';
  }

  @Override
  protected String defaultConstraintName() {
    return "unique_" + columns().stream()
                                .map(String::toLowerCase)
                                .collect(Collectors.joining("_")) +
        '_' + Strings.random(4);
  }
}
