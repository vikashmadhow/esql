/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A primary key constraint on a table.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
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
  public String translate(Target target, Map<String, Object> parameters) {
    return "constraint "
         + '"' + (name() != null ? name() : defaultConstraintName(target, namePrefix())) + '"'
         + " primary key(" + quotedColumnsList(columns()) + ')';
  }

  @Override
  protected String namePrefix() {
    return "pk_";
  }
}
