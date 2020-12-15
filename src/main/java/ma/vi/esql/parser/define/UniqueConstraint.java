/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
      return new HashSet<>(c.columns()).equals(new HashSet<>(columns()));
    }
    return false;
  }

  @Override
  public String translate(Target target) {
    return "constraint \"" + (name() != null ? name() : defaultConstraintName()) +
        "\" unique(" + quotedColumnsList(columns()) + ')';
  }

  @Override
  protected String defaultConstraintName() {
    return "unique_" + columns().stream()
                                .map(String::toLowerCase)
                                .collect(Collectors.joining("_")) +
        '_' + Strings.random(4);
  }
}
