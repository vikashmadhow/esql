/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.AliasedRelation;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Relation;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Macro;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.expression.ColumnRef;

import java.util.*;

import static java.util.stream.Collectors.joining;

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
   * if it is not a parameter to a function.
   */
  @Override
  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
    QueryUpdate query = path.ancestor(QueryUpdate.class);
    TableExpr from = query.tables();
    Relation fromType = from.type(path.add(from));

    boolean changed = false;
    Map<String, String> aliased = new HashMap<>();
    Map<String, Column> resolvedColumns = new LinkedHashMap<>();
    for (Column column: columns()) {
      if (column instanceof StarColumn all) {
        changed = true;
        String qualifier = all.qualifier();
        Relation rel = qualifier == null ? fromType : fromType.forAlias(qualifier);
        for (Column relCol: rel.columns()) {
          String alias = relCol.alias();
          if (alias == null) {
            alias = Strings.makeUnique(resolvedColumns.keySet(), "col", false);
          }
          Column col;
          if (rel instanceof BaseRelation
           || (rel instanceof AliasedRelation && ((AliasedRelation)rel).relation instanceof BaseRelation)) {
            col = relCol.copy();
            if (qualifier != null) {
              ColumnRef.qualify(col.expression(), qualifier, true);
            }
            if (col.metadata() != null) {
              for (Attribute attr: col.metadata().attributes().values()) {
                ColumnRef.qualify(attr.attributeValue(), qualifier, true);
              }
            }
          } else {
            col = new Column(context,
                             alias,
                             new ColumnRef(context, qualifier, relCol.alias()),
                             null);
          }

          int pos = alias.indexOf('/');
          if (pos == -1) {
            /*
             * Normal column (not metadata).
             */
            if (resolvedColumns.containsKey(alias)) {
              alias = Strings.makeUnique(resolvedColumns.keySet(), alias, false);
            }
            if (relCol.alias() != null && !alias.equals(relCol.alias())) {
              aliased.put(relCol.alias(), alias);
              col = col.copy(alias);
            }
            resolvedColumns.put(alias, col);

          } else if (pos > 0) {
            /*
             * Column metadata
             */
            String columnName = alias.substring(0, pos);
            if (aliased.containsKey(columnName)) {
              // replace column name with replacement if the column name was changed
              String aliasName = aliased.get(columnName);
              alias = aliasName + alias.substring(pos);
              col.alias(alias);
            }
            resolvedColumns.put(alias, col);

          } else if (!resolvedColumns.containsKey(alias)) {
            /*
             * table metadata first encounter (by elimination, pos==0 in this case)
             */
            resolvedColumns.put(alias, col);
          }
        }
      }
//      else {
//        Expression<?, String> colExpr = column.expression();
//        Expression<?, String> expr = (Expression<?, String>)colExpr.map((e, p) -> {
//          if (e instanceof ColumnRef c) {
//            SelectExpression selExpr = p.ancestor(SelectExpression.class);
//            if (selExpr == null) {
//              if (c.qualifier() == null) {
//                Column col = fromType.column(c.name());
//                return col.expression();
//              }
//            }
//          }
//          return e;
//        }, null, path.add(colExpr));
//        if (expr != colExpr) {
//          changed = true;
//          resolvedColumns.put(column.alias(), column.expression(expr));
//        } else {
//          resolvedColumns.put(column.alias(), column);
//        }
//      }
    }
    return changed ? new ColumnList(context, new ArrayList<>(resolvedColumns.values()))
                   : esql;
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