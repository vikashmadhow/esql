/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.expression.ColumnRef;
import ma.vi.esql.parser.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a check constraint defined on a table.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CheckConstraint extends ConstraintDefinition {
  public CheckConstraint(Context context, String name, Expression<?, String> expression) {
    super(context, name, T2.of("expr", expression));
    List<String> columns = new ArrayList<>();
    expression.forEach(e -> {
      if (e instanceof ColumnRef) {
        columns.add(((ColumnRef)e).name());
      }
      return true;
    });
    columns(columns);
  }

  public CheckConstraint(CheckConstraint other) {
    super(other);
  }

  @Override
  public CheckConstraint copy() {
    if (!copying()) {
      try {
        copying(true);
        return new CheckConstraint(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public boolean sameAs(ConstraintDefinition def) {
    if (def instanceof CheckConstraint) {
      CheckConstraint c = (CheckConstraint)def;
      return c.expr().equals(expr());
    }
    return false;
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    return "constraint "
        + '"' + (name() != null ? name() : defaultConstraintName(target, namePrefix())) + '"'
        + " check(" + expr().translate(target, parameters) + ')';
  }

  @Override
  protected String namePrefix() {
    return "ck_";
  }

  public Expression<?, String> expr() {
    return child("expr");
  }
}
