/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.AmbiguousColumnException;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.UntypedMacro;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.SelectExpression;
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
    QueryUpdate query = path.ancestor(QueryUpdate.class);
    TableExpr from = query.tables();

    boolean expandColumns = !query.grouped() && !path.hasAncestor(SelectExpression.class);
    if (expandColumns) {
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
                                                              q -> from.named(q.equals("/") ? null : q)
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
      } else if (expandColumns
              && column.name() != null
              && column.name().indexOf('/') == -1
              && column.expression() instanceof ColumnRef ref) {
        /*
         * Get expansion (metadata columns along with column) for referred column.
         */
        boolean renamed = !ref.name().equals(column.name());
        if (renamed) {
          aliased.put(ref.name(), column.name());
        }
        String qualifier = ref.qualifier();
        List<Column> columnList = columnLists.computeIfAbsent(qualifier == null ? "/" : qualifier,
                                                              q -> from.named(q.equals("/") ? null : q)
                                                                       .columnList(path))
                                             .stream()
                                             .filter(c -> c.name().equals(ref.name())
                                                       || c.name().startsWith(ref.name() + '/'))
                                             .toList();
        if (columnList.stream().filter(c -> c.name().equals(ref.name())).count() > 1) {
          throw new AmbiguousColumnException("Ambiguous column " + ref + " exists in "
                                           + "multiple relations in " + from);
        }
        for (Column col: columnList) {
          if (col.name().equals(ref.name())) {
            Column c = new Column(col.context,
                                  column.name(),
                                  qualifier != null ? qualify(col.expression(), qualifier)
                                                    : col.expression(),
                                  col.type(),
                                  null);
            cols.add(c);
            colsMap.put(c.name(), c);

          } else {
            /*
             * Add metadata column, renaming if aliased.
             */
            String newName = renamed
                           ? column.name() + col.name().substring(ref.name().length())
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
        if (column.metadata() != null && !column.metadata().attributes().isEmpty()) {
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
      } else if (column.name() == null || !colsMap.containsKey(column.name())) {
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

//  /**
//   * Expands star columns (*) to the individual columns of the referred table
//   * when it is part of the column list of a query, set column names when not
//   * specified and disambiguate duplicate column names.
//   */
//  @Override
//  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
//    QueryUpdate query = path.ancestor(QueryUpdate.class);
//    TableExpr from = query.tables();
//    Relation fromType = from.computeType(path.add(from));
//
//    boolean expandColumns = !query.grouped() && !path.hasAncestor(SelectExpression.class);
//    if (expandColumns) {
//      Cte cte = path.ancestor(Cte.class);
//      if (cte != null && cte.fields() != null && !cte.fields().isEmpty()) {
//        expandColumns = false;
//      }
//    }
//
//    boolean changed = false;
//    int colIndex = 1;
//
//    Set<String> existingColNames = columns().stream().map(Column::name).collect(toSet());
//    Set<String> resolvedColNames = new HashSet<>();
//    Map<String, String> aliased = new HashMap<>();
//    Map<String, Column> resolvedColumns = new LinkedHashMap<>();
//    for (Column column: columns()) {
//      if (column instanceof StarColumn all) {
//        changed = true;
//        String qualifier = all.qualifier();
//        for (T2<Relation, Column> relCol: fromType.columns()) {
//          /*
//           * Ensure each column has a name, and it is unique in the column list.
//           */
//          Column col = relCol.b;
//          String colName = col.name();
//          if (colName == null) {
//            T2<String, Integer> newName = makeUnique("column", resolvedColNames, colIndex, true);
//            colName = newName.a;
//            colIndex = newName.b;
//
//          } else {
//            int pos = colName.indexOf('/');
//            if (pos == -1) {
//              /*
//               * Normal column (not metadata).
//               */
//              if (resolvedColNames.contains(colName)) {
//                T2<String, Integer> newName = makeUnique(colName, resolvedColNames, colIndex, false);
//                colName = newName.a;
//                colIndex = newName.b;
//                aliased.put(col.name(), colName);
//              }
//            } else if (pos > 0) {
//              /*
//               * Column metadata
//               */
//              String columnName = colName.substring(0, pos);
//              if (aliased.containsKey(columnName)) {
//                /*
//                 * replace column name with replacement if the column name was changed
//                 */
//                String aliasName = aliased.get(columnName);
//                colName = aliasName + colName.substring(pos);
//              }
//            }
//          }
//
//          if (!resolvedColumns.containsKey(colName)) {
//            /*
//             * Qualify column expressions (needed for derived columns) and metadata
//             * expressions when selecting from base relations. For non-base relations,
//             * the selected expression is a column reference to the underlying column
//             * name.
//             */
//            Relation rel = relCol.a;
//            if (rel instanceof BaseRelation
//            || (rel instanceof AliasedRelation ar && ar.relation instanceof BaseRelation)) {
//              Expression<?, String> expr = col.expression();
//              if (qualifier != null) {
//                expr = qualify(expr, qualifier);
//              } else if (rel.alias() != null) {
//                expr = qualify(expr, rel.alias());
//              }
//              Metadata metadata = null;
//              if (col.metadata() != null) {
//                List<Attribute> attributes = new ArrayList<>();
//                for (Attribute attr: col.metadata().attributes().values()) {
//                  attributes.add(new Attribute(context, attr.name(), qualify(attr.attributeValue(), qualifier)));
//                }
//                metadata = new Metadata(context, attributes);
//              }
//              col = new Column(context, colName, expr, metadata);
//            } else {
//              col = new Column(context,
//                               colName,
//                               new ColumnRef(context, qualifier != null ? qualifier : rel.alias(), col.name()),
//                               null);
//            }
//
//            /*
//             * For table metadata (starting with '/') not disambiguation is performed,
//             * only consider first encounter.
//             */
//            resolvedColumns.put(colName, col);
//            resolvedColNames.add(colName);
//          }
//        }
//      } else {
//        ColumnRef ref = ColumnRef.from(column.expression());
//        Column changedCol = column;
//        if (column.name() == null) {
//          changed = true;
//          String prefix = ref != null ? ref.name() : "column";
//          T2<String, Integer> newName = makeUnique(prefix, resolvedColNames, colIndex, ref == null);
//          colIndex = newName.b;
//          changedCol = column.name(newName.a);
//
//        } else {
//          String colName = column.name();
//          if (!resolvedColNames.contains(colName)) {
//            resolvedColNames.add(colName);
//          } else {
//            changed = true;
//            T2<String, Integer> newName = makeUnique(colName, resolvedColNames, colIndex, false);
//            colIndex = newName.b;
//            changedCol = column.name(newName.a);
//          }
//        }
//        resolvedColumns.put(changedCol.name(), changedCol);
//        resolvedColNames.add(changedCol.name());
//
//        if (ref != null && !ref.name().equals(changedCol.name())) {
//          aliased.put(ref.name(), changedCol.name());
//        }
//
//        Map<String, Column> metadataCols = new LinkedHashMap<>();
//        if (expandColumns) {
//          /*
//           * Get column metadata from relation.
//           */
//          if (ref != null && !ref.name().contains("/")) {
//            for (T2<Relation, Column> relCol: fromType.columns(ref.name())) {
//              Relation rel = relCol.a;
//              if (Objects.equals(rel.alias(), ref.qualifier())) {
//                Column col = relCol.b;
//                if (!col.name().equals(ref.name())) {
//                  metadataCols.put(col.name(), col);
//                }
//              }
//            }
//          }
//        }
//        if (column.metadata() != null && !column.metadata().attributes().isEmpty()) {
//          /*
//           * Override with explicit column metadata in column list.
//           */
//          for (Attribute att: column.metadata().attributes().values()) {
//            for (Column col: columnsForAttribute(att, changedCol.name() + '/', aliased)) {
//              metadataCols.put(col.name(), ref != null && ref.qualifier() != null
//                                         ? qualify(col, ref.qualifier())
//                                         : col);
//            }
//          }
//        }
//        if (!metadataCols.isEmpty()) {
//          for (Map.Entry<String, Column> col: metadataCols.entrySet()) {
//            if (!existingColNames.contains(col.getKey())) {
//               changed = true;
//               resolvedColumns.put(col.getKey(), col.getValue());
//               resolvedColNames.add(col.getKey());
//            }
//          }
//        }
//      }
//    }
//    if (expandColumns && !columns().isEmpty()) {
//      /*
//       * Add relation metadata if the column list is not empty (which would be
//       * the case in general for the returning clause of a modifying statement;
//       * in those cases adding the relation metadata would result in the modifying
//       * statement to returning data when that is clearly contrary to the intention
//       * of the statement).
//       */
//      for (T2<Relation, Column> relCol: fromType.columns().stream()
//                                                .filter(c -> c.b.name().startsWith("/")).toList()) {
//        Column col = relCol.b;
//        if (!existingColNames.contains(col.name())) {
//          changed = true;
//          resolvedColumns.put(col.name(), col);
//        }
//      }
//    }
//    return changed ? new ColumnList(context, new ArrayList<>(resolvedColumns.values()))
//                   : esql;
//  }

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
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    return columns().stream()
                    .map(c -> c.trans(target, path, parameters))
                    .collect(joining(", "));
  }

  public List<Column> columns() {
    return children();
  }
}