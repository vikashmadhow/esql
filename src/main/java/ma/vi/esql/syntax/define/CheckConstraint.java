/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ma.vi.esql.syntax.Translatable.Target.MARIADB;
import static ma.vi.esql.syntax.Translatable.Target.MYSQL;

/**
 * Represents a check constraint defined on a table.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CheckConstraint extends ConstraintDefinition {
  public CheckConstraint(Context context,
                         String name,
                         String table,
                         Expression<?, String> expression) {
    super(context,
          name != null ? name : defaultConstraintName("ck_", columnsInExpression(expression)),
          table,
          columnsInExpression(expression),
          T2.of("expr", expression));
  }

  public CheckConstraint(CheckConstraint other) {
    super(other);
  }

  @SafeVarargs
  public CheckConstraint(CheckConstraint other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public CheckConstraint copy() {
    return new CheckConstraint(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public CheckConstraint copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new CheckConstraint(this, value, children);
  }

  private static List<String> columnsInExpression(Expression<?, String> expression) {
    List<String> columns = new ArrayList<>();
    expression.forEach((esql, path) -> {
      if (esql instanceof ColumnRef) {
        columns.add(((ColumnRef)esql).name());
      }
      return true;
    });
    return columns;
  }

  @Override
  public boolean sameAs(ConstraintDefinition def) {
    if (def instanceof CheckConstraint c) {
      return c.expr().equals(expr());
    }
    return false;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    String name = name();
    if (name.length() >= 64 && (target == MARIADB || target == MYSQL)) {
      /*
       * Identifiers in MySQL and MariaDB are limited to 64 characters.
       */
      name = name.substring(name.length() - 64);
    }
    return "constraint "
        + '"' + name + '"'
        + " check(" + expr().translate(target, path.add(expr()), parameters) + ')';
  }

  public Expression<?, String> expr() {
    return child("expr");
  }
}
