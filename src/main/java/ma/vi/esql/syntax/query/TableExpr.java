/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Relation;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

/**
 * The table expression in the from clause of a select, update or delete statement
 *
 * @author vikash.madhow@gmail.com
 */
public abstract class TableExpr extends Esql<String, String> {
  @SafeVarargs
  public TableExpr(Context context,
                   String value,
                   T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public TableExpr(TableExpr other) {
    super(other);
  }

  @SafeVarargs
  public TableExpr(TableExpr other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract TableExpr copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract TableExpr copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  /**
   * Returns true if the table(s) in this table expression exists.
   */
  public abstract boolean exists();

  @Override
  public abstract Relation type(EsqlPath path);
}
