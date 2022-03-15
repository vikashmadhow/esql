/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Relation;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.exec.Filter;

import java.util.List;
import java.util.Set;

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

  public record ShortestPath(SingleTableExpr    source,
                             BaseRelation.Path  path,
                             Filter filter) {}

  public record AppliedShortestPath(ShortestPath path,
                                    TableExpr    result,
                                    String       targetAlias) {}

  public abstract ShortestPath findShortestPath(Filter filter);
  public abstract AppliedShortestPath applyShortestPath(ShortestPath shortest);

  /**
   * Returns true if the table(s) in this table expression exists.
   */
  public abstract boolean exists(EsqlPath path);

  /**
   * Return the list of columns for this table expression. This method is called
   * before type inference and thus should not depend on any type information for
   * its function.
   */
  public abstract List<Column> columnList(EsqlPath path);

  /**
   * Returns the list of smallest table expressions composing this table expression.
   * If this table expression cannot be split into smaller table expressions a list
   * containing this table expression as the single element is returned.
   * E.g., join table expression `a join b join c join d` will return [a, b, c, d]
   * while single table expression `x.y.z` will [x.y.z].
   */
  public abstract List<TableExpr> tables();

  /**
   * @return The set of all table aliases for all tables in the table expression.
   */
  public abstract Set<String> aliases();

  /**
   * Returns the table expression with the specified alias, which can be the alias
   * associated to this table expression or to one of its sub-components (as is
   * the case for joins).
   */
  public abstract TableExpr aliased(String name);

  @Override
  public abstract Relation computeType(EsqlPath path);
}
