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
         + " primary key(" + quotedColumnsList(columns()) + ')';
  }

  @Override
  protected String namePrefix() {
    return "pk_";
  }
}
