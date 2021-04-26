/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.type.Relation;

/**
 * The table expression in the from clause of a select, update or delete statement
 *
 * @author vikash.madhow@gmail.com
 */
public abstract class TableExpr extends Esql<String, String> {
  public TableExpr(Context context, String value, T2<String, Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public TableExpr(TableExpr other) {
    super(other);
  }

  /**
   * Returns the table expression with the specified alias in this
   * table expression. This could be this table itself or a joined
   * table or table expression, for instance.
   */
  public abstract TableExpr forAlias(String alias);

  @Override
  public abstract TableExpr copy();

  @Override
  public abstract Relation type();
}
