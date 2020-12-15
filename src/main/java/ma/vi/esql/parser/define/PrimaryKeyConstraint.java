/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class PrimaryKeyConstraint extends ConstraintDefinition {
  public PrimaryKeyConstraint(Context context, String name, List<String> columns) {
    super(context, name, T2.of("columns", new Esql<>(context, columns)));
  }

  public PrimaryKeyConstraint(PrimaryKeyConstraint other) {
    super(other);
  }

  @Override
  public PrimaryKeyConstraint copy() {
    if (!copying()) {
      try {
        copying(true);
        return new PrimaryKeyConstraint(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  public boolean sameAs(ConstraintDefinition def) {
    if (def instanceof PrimaryKeyConstraint) {
      PrimaryKeyConstraint c = (PrimaryKeyConstraint)def;
      return new HashSet<>(c.columns()).equals(new HashSet<>(columns()));
    }
    return false;
  }

  @Override
  public String translate(Target target) {
    return "constraint \"" + (name() != null ? name() : defaultConstraintName()) +
        "\" primary key(" + quotedColumnsList(columns()) + ')';
  }

  @Override
  protected String defaultConstraintName() {
    return "primary_" + columns().stream()
                                 .map(String::toLowerCase)
                                 .collect(Collectors.joining("_")) +
        '_' + Strings.random(4);
  }
}
