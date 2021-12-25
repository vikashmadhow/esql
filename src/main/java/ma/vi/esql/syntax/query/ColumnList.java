/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.AliasedRelation;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Relation;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Macro;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;

import java.util.*;

import static java.util.stream.Collectors.joining;
import static ma.vi.esql.syntax.expression.ColumnRef.qualify;

/**
 * A list of attributes (name expression pairs) describing
 * certain parts of queries, tables, etc. This is also used as
 * the update set clause as it has the same structure.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.mail)
 */
public class ColumnList extends Esql<String, String> implements Macro {
  public ColumnList(Context context, List<Column> columns) {
    super(context, columns, true);
  }

  public ColumnList(ColumnList other) {
    super(other);
  }

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
    Relation fromType = null;

    boolean changed = false;
    int colIndex = 1;
    Set<String> colNames = new HashSet<>();
    Map<String, String> aliased = new HashMap<>();
    Map<String, Column> resolvedColumns = new LinkedHashMap<>();
    for (Column column: columns()) {
      if (column instanceof StarColumn all) {
        changed = true;
        String qualifier = all.qualifier();
        if (fromType == null) {
          fromType = from.type(path.add(from));
        }
        for (T2<Relation, Column> relCol: fromType.columns()) {
          /*
           * Ensure each column has a name, and it is unique in the column list.
           */
          Column col = relCol.b;
          String colName = col.name();
          if (colName == null) {
            T2<String, Integer> newName = makeUnique("column", colNames, colIndex, true);
            colName = newName.a;
            colIndex = newName.b;

          } else {
            int pos = colName.indexOf('/');
            if (pos == -1) {
              /*
               * Normal column (not metadata).
               */
              if (colNames.contains(colName)) {
                T2<String, Integer> newName = makeUnique(colName, colNames, colIndex, false);
                colName = newName.a;
                colIndex = newName.b;
                aliased.put(col.name(), colName);
              }
            } else if (pos > 0) {
              /*
               * Column metadata
               */
              String columnName = colName.substring(0, pos);
              if (aliased.containsKey(columnName)) {
                /*
                 * replace column name with replacement if the column name was changed
                 */
                String aliasName = aliased.get(columnName);
                colName = aliasName + colName.substring(pos);
              }
            }
          }

          if (!resolvedColumns.containsKey(colName)) {
            /*
             * Qualify column expressions (needed for derived columns) and metadata
             * expressions when selecting from base relations. For non-base relations,
             * the selected expression is a column reference to the underlying column
             * name.
             */
            Relation rel = relCol.a;
            if (rel instanceof BaseRelation
                || (rel instanceof AliasedRelation && ((AliasedRelation)rel).relation instanceof BaseRelation)) {
              Expression<?, String> expr = col.expression();
              if (qualifier != null) {
                expr = qualify(expr, qualifier);
              }
              Metadata metadata = null;
              if (col.metadata() != null) {
                List<Attribute> attributes = new ArrayList<>();
                for (Attribute attr: col.metadata().attributes().values()) {
                  attributes.add(new Attribute(context, attr.name(), qualify(attr.attributeValue(), qualifier)));
                }
                metadata = new Metadata(context, attributes);
              }
              col = new Column(context, colName, expr, metadata);
            } else {
              col = new Column(context,
                               colName,
                               new ColumnRef(context, qualifier, col.name()),
                               null);
            }

            /*
             * For table metadata (starting with '/') not disambiguation is performed,
             * only consider first encounter.
             */
            resolvedColumns.put(colName, col);
            colNames.add(colName);
          }
        }
      } else if (column.name() == null) {
        changed = true;
        Expression<?, String> expr = column.expression();
        String prefix = expr instanceof ColumnRef ref ? ref.name() : "column";
        T2<String, Integer> newName = makeUnique(prefix, colNames, colIndex, !(expr instanceof ColumnRef));
        colIndex = newName.b;
        Column col = column.name(newName.a);
        resolvedColumns.put(col.name(), col);

      } else {
        String colName = column.name();
        if (!colNames.contains(colName)) {
          colNames.add(colName);
        } else {
          changed = true;
          T2<String, Integer> newName = makeUnique(colName, colNames, colIndex, false);
          colIndex = newName.b;
          column = column.name(newName.a);
        }
        resolvedColumns.put(column.name(), column);
      }
    }
    return changed ? new ColumnList(context, new ArrayList<>(resolvedColumns.values()))
                   : esql;
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
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return columns().stream()
                    .map(c -> c.trans(target, path, parameters))
                    .collect(joining(", "));
  }

  public List<Column> columns() {
    return children();
  }
}