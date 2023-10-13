/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.composable.Composable;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Relation;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.ColumnRef;

import java.util.List;
import java.util.Set;

/**
 * The table expression in the from clause of a select, update or delete statement.
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
   * Find the shortest path between the tables in this table expression and the
   * filtered table, if any. Null if no path exists.
   * @param composable Filter whose table is the target of the shortest path.
   * @return A shortest path, if found, or null.
   */
  public abstract ShortestPath findShortestPath(Composable composable);

  /**
   * Applies the shortest path found by {@link #findShortestPath(Composable)} to this
   * table expression, returning the result of this application containing the
   * changed table expression and the alias set on the target table, that can be
   * used to modify the filter condition.
   *
   * @param shortest Shortest path to apply.
   * @return The result of applying the shortest path.
   */
  public abstract AppliedShortestPath applyShortestPath(ShortestPath shortest, TableExpr root);

  /**
   * The shortest path found by {@link #findShortestPath(Composable)} for a filter
   * consists of the source table of a path and the path itself consisting of a
   * sequence of links (foreign keys or reverse foreign keys).
   */
  public record ShortestPath(SingleTableExpr    source,
                             BaseRelation.Path  path,
                             Composable         composable) {}

  /**
   * The application of a shortest path produced by {@link #applyShortestPath(ShortestPath, TableExpr)}
   * consists of the resulting table expression and the alias of the target table
   * which can be used to modify the filter condition, if needed.
   */
  public record AppliedShortestPath(ShortestPath path,
                                    TableExpr    result,
                                    String       targetAlias) {}

  /**
   * Returns true if the table(s) in this table expression exists.
   */
  public abstract boolean exists(EsqlPath path);

  /**
   * Return the list of columns for this table expression. This method is called
   * before type inference and thus should not depend on any type information to
   * function.
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
   * associated to this table expression or to one of its subcomponents (as is
   * the case for joins).
   */
  public abstract TableExpr aliased(String name);

  /**
   * Returns the table expression for the specified table, if it is part of this
   * table expression.
   */
  public abstract TableExpr table(String name);

  @Override
  public abstract Relation computeType(EsqlPath path);

  /**
   * Return a reference to the named column of the specified table if the table
   * is part of this table expression and it contains the specified column.
   */
  public abstract ColumnRef findColumn(String table, String name);
}
