/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * A table expression which can be aliased (a single table, a select expression
 * or dynamic table expression).
 *
 * @author vikash.madhow@gmail.com
 */
public abstract class AbstractAliasTableExpr extends TableExpr {
  public AbstractAliasTableExpr(Context context,
                                String value,
                                String alias,
                                T2<String, ? extends Esql<?, ?>>... children) {
    super(context,
          value,
          Stream.concat(
              Stream.of(T2.of("alias", new Esql<>(context, alias))),
              Arrays.stream(children)).toArray(T2[]::new));
  }

  public AbstractAliasTableExpr(AbstractAliasTableExpr other) {
    super(other);
  }

  public AbstractAliasTableExpr(AbstractAliasTableExpr other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract AbstractAliasTableExpr copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract AbstractAliasTableExpr copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  public String alias() {
    return childValue("alias");
  }

  @Override
  public TableExpr forAlias(String alias) {
    return alias.equals(alias()) ? this : null;
  }
}