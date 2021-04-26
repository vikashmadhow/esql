/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;

/**
 * A table expression which can be aliased (a single table, a select expression
 * or dynamic table expression).
 *
 * @author vikash.madhow@gmail.com
 */
public abstract class AbstractAliasTableExpr extends TableExpr {
  public AbstractAliasTableExpr(Context context, String value, String alias) {
    super(context, value, T2.of("alias", new Esql<>(context, alias)));
  }

  public AbstractAliasTableExpr(AbstractAliasTableExpr other) {
    super(other);
  }

  @Override
  public abstract AbstractAliasTableExpr copy();

  public String alias() {
    return childValue("alias");
  }

  @Override
  public TableExpr forAlias(String alias) {
    return alias.equals(alias()) ? this : null;
  }
}