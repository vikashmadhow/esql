/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.AmbiguousColumnException;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Define;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.SelectExpression;
import ma.vi.esql.syntax.macro.UntypedMacro;
import ma.vi.esql.syntax.modify.UpdateSet;
import org.pcollections.PMap;

import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static ma.vi.esql.syntax.expression.ColumnRef.qualify;

/**
 * A list of attributes (name expression pairs) describing
 * certain parts of queries, tables, etc. This is also used as
 * the update set clause as it has the same structure.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.mail)
 */
public class ColumnList extends Esql<String, String> implements UntypedMacro {
  public ColumnList(Context context, List<Column> columns) {
    super(context, columns, true);
  }

  public ColumnList(ColumnList other) {
    super(other);
  }

  @SafeVarargs
  public ColumnList(ColumnList other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public ColumnList copy() {
    return new ColumnList(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public ColumnList copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new ColumnList(this, value, children);
  }

  /**
   * Expands star columns (*) to the individual columns of the referred table
   * when it is part of the column list of a query, set column names when not
   * specified and disambiguate duplicate column names.
   */
  @Override
  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
    if (!path.hasAncestor(Define.class)) {
      QueryUpdate query = path.ancestor(QueryUpdate.class);
      TableExpr from = query.tables();

      boolean expandColumns = !query.grouped()
                           && !path.hasAncestor(SelectExpression.class)
                           && !path.hasAncestor(UpdateSet.class);
      if (expandColumns) {
        /*
         * Do not expand columns if this column list is part of a query which is
         * in the column list of an outer query or part of a where clause, as the
         * column list of this query, in such cases, is limited to one column only.
         */
        T2<QueryUpdate, EsqlPath> p = path.ancestorAndPath(QueryUpdate.class);
        if (p != null
         && p.b.tail() != null
         && (p.b.tail().ancestor("columns") != null
          || p.b.tail().ancestor("where")   != null
          || p.b.tail().ancestor("orderBy") != null
          || p.b.tail().ancestor("groupBy") != null
          || p.b.tail().ancestor("having")  != null)) {
          expandColumns = false;
        }
      }
      if (expandColumns) {
        /*
         * Do not expand columns if this column list is part of a CTE that has
         * explicit fields defined (as the column list of this query must match
         * the CTR fields exactly).
         */
        Cte cte = path.ancestor(Cte.class);
        if (cte != null && cte.fields() != null && !cte.fields().isEmpty()) {
          expandColumns = false;
        }
      }

      String fromAlias = null;
      if (from instanceof AbstractAliasTableExpr aliased) {
        fromAlias = aliased.alias();
      }

      boolean changed = false;
      List<Column> cols = new ArrayList<>();
      Map<String, Column> colsMap = new HashMap<>();
      Map<String, List<Column>> columnLists = new HashMap<>();
      Map<String, String> aliased = new HashMap<>();
      for (Column column: columns()) {
        if (column instanceof StarColumn all) {
          changed = true;
          String qualifier = all.qualifier();
          List<Column> columnList = columnLists.computeIfAbsent(qualifier == null ? "/" : qualifier,
                                                                q -> from.aliased(q.equals("/") ? null : q)
                                                                         .columnList(path));
          if (qualifier != null || fromAlias != null) {
            for (Column col: columnList) {
              Column c = new Column(col.context,
                                    col.name(),
                                    qualifier != null ? qualify(col.expression(), qualifier)
                                  : fromAlias != null ? qualify(col.expression(), fromAlias)
                                  : col.expression(),
                                    col.type(),
                                    null);
              cols.add(c);
              colsMap.put(c.name(), c);
            }
          } else {
            cols.addAll(columnList);
            colsMap.putAll(columnList.stream()
                                     .collect(toMap(Column::name,
                                                    identity(),
                                                    (c1, c2) -> c2)));
          }
        } else if (column.name() != null
                && column.name().indexOf('/') == -1
                && column.expression() instanceof ColumnRef ref) {
          /*
           * Get expansion (metadata columns along with column) for referred column.
           */
          boolean renamed = !ref.columnName().equals(column.name());
          if (renamed) {
            aliased.put(ref.columnName(), column.name());
          }
          String qualifier = ref.qualifier();
          List<Column> columnList = columnLists.computeIfAbsent(qualifier == null ? "/" : qualifier,
                                                                q -> from.aliased(q.equals("/") ? null : q)
                                                                         .columnList(path))
                                               .stream()
                                               .filter(c -> c.name().equals(ref.columnName())
                                                         || c.name().startsWith(ref.columnName() + '/'))
                                               .toList();
          if (columnList.stream().filter(c -> c.name().equals(ref.columnName())).count() > 1) {
            throw new AmbiguousColumnException("Ambiguous column " + ref + " exists in "
                                             + "multiple relations in " + from);
          }
          for (Column col: columnList) {
            if (col.name().equals(ref.columnName())) {
              Column c = new Column(col.context,
                                    column.name(),
                                    qualifier != null ? qualify(col.expression(), qualifier)
                                                      : col.expression(),
                                    col.type(),
                                    null);
              cols.add(c);
              colsMap.put(c.name(), c);

            } else if (expandColumns) {
              /*
               * Add metadata column, renaming if aliased.
               */
              String newName = renamed
                             ? column.name() + col.name().substring(ref.columnName().length())
                             : col.name();
              if (!colsMap.containsKey(newName)) {
                Column c = new Column(col.context,
                                      newName,
                                      qualifier != null ? qualify(col.expression(), qualifier)
                                                        : col.expression(),
                                      col.type(),
                                      null);
                cols.add(c);
                colsMap.put(c.name(), c);
              }
            }
          }

          /*
           * Override with explicit metadata columns.
           */
          if (expandColumns
           && column.metadata() != null
           && !column.metadata().attributes().isEmpty()) {
            for (Attribute att: column.metadata().attributes().values()) {
              for (Column col: BaseRelation.columnsForAttribute(att, column.name() + '/', aliased)) {
                if (!colsMap.containsKey(col.name())
                 || !colsMap.get(col.name()).expression().equals(col.expression())) {
                  if (colsMap.containsKey(col.name())) {
                    cols.removeIf(c -> col.name().equals(c.name()));
                  }
                  Column c = new Column(col.context,
                                        col.name(),
                                        qualifier != null ? qualify(col.expression(), qualifier)
                                                        : col.expression(),
                                        col.type(),
                                        null);
                  cols.add(c);
                  colsMap.put(c.name(), c);
                }
              }
            }
          }
        } else if (column.name() == null
                || !colsMap.containsKey(column.name())) {
          cols.add(column);
          if (column.name() != null) {
            colsMap.put(column.name(), column);
          }
        }
      }
      changed |= cols.size() != columns().size()
             || !cols.stream()
                     .map(Column::name)
                     .collect(toSet()).equals(columns().stream()
                                                       .map(Column::name)
                                                       .collect(toSet()));
      List<Column> targetCols = new ArrayList<>();
      changed |= disambiguateNames(cols, targetCols);
      return changed ? new ColumnList(context, targetCols) : esql;
    }
    return esql;
  }

  public static boolean disambiguateNames(List<Column> sourceColumns, List<Column> targetColumns) {
    int colIndex = 1;
    boolean changed = false;
    Set<String> names = new HashSet<>();
    Map<String, String> aliased = new HashMap<>();
    for (Column col: sourceColumns) {
      if (col.name() == null) {
        changed = true;
        T2<String, Integer> name = makeUnique("column", names, colIndex, true);
        targetColumns.add(col.name(name.a));
        colIndex = name.b;

      } else if (names.contains(col.name())) {
        changed = true;
        int pos = col.name().indexOf('/');
        if (pos == -1) {
          /*
           * Normal column
           */
          T2<String, Integer> name = makeUnique(col.name(), names, colIndex, false);
          aliased.put(col.name(), name.a);
          targetColumns.add(col.name(name.a));
          colIndex = name.b;

        } else if (pos == 0) {
          /*
           * Table metadata column: only show first column in case of duplicates.
           */
          if (!names.contains(col.name())) {
            names.add(col.name());
            targetColumns.add(col);
          }
        } else {
          /*
           * Column metadata column: disambiguate base column name.
           */
          String columnName = col.name().substring(0, pos);
          if (aliased.containsKey(columnName)) {
            String alias = aliased.get(columnName);
            String colName = alias + col.name().substring(pos);
            if (!names.contains(colName)) {
              names.add(colName);
              targetColumns.add(col.name(colName));
            }
          }
        }
      } else {
        names.add(col.name());
        targetColumns.add(col);
      }
    }
    return changed;
  }

  /**
   * Produces a unique name within the set of names by repeatedly incrementing an
   * index and adding to the prefix. Returns the unique name and the index to use
   * (which the index used in the current returned name + 1).
   *
   * @param prefix Name prefix.
   * @param names Set of names to test unicity against. The new name is added to
   *              this set.
   * @param index The index to start adding to the prefix.
   * @param firstNameWithIndex When this is true the first unique name is produced
   *                           by adding the index to the prefix. When it is false,
   *                           the first unique name to test is just the prefix.
   * @return The pair of unique name produced and the next index to use for
   *         producing unique names using this method.
   */
  public static T2<String, Integer> makeUnique(String prefix,
                                               Set<String> names,
                                               int index,
                                               boolean firstNameWithIndex) {
    String name = prefix + (firstNameWithIndex ? String.valueOf(index++) : "");
    while (names.contains(name)) {
      name = prefix + index;
      index++;
    }
    names.add(name);
    return T2.of(name, index);
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return columns().stream()
                    .map(c -> c.translate(target, null, path, parameters, null))
                    .collect(joining(", "));
  }

  public List<Column> columns() {
    return children();
  }
}