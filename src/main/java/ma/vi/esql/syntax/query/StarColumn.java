/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * The wild-card (*) representing all columns in a query.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class StarColumn extends Column {
  public StarColumn(Context context, String qualifier) {
    super(context, "_", null, null,
          T2.of("qualifier", new Esql<>(context, qualifier)));
  }

  public StarColumn(StarColumn other) {
    super(other);
  }

  public StarColumn(StarColumn other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public StarColumn copy() {
    return new StarColumn(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public StarColumn copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new StarColumn(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    switch (target) {
      case JSON:
      case JAVASCRIPT:
      case ESQL:
        String e = (qualifier() != null ? qualifier() + '.' : "") + "*";
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return (qualifier() != null ? '"' + qualifier() + "\"." : "") + "*";
    }
  }

  /**
   * Expands star columns (*) to the individual columns of the referred table
   * if it is not a parameter to a function.
   */
//  @Override
//  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
//    /*
//     * Expand star columns (*) to the individual columns they refer to
//     */
//    Select select = path.ancestor(Select.class);
//    if (select == null) {
//      throw new TranslationException("* specified outside of a Select");
//    }
//    TableExpr from = select.tables();
//    Relation fromType = from.type(path);
//
//    Map<String, String> aliased = new HashMap<>();
//    Map<String, Column> resolvedColumns = new LinkedHashMap<>();
//
//    String qualifier = qualifier();
//    Relation rel = qualifier == null ? fromType : fromType.forAlias(qualifier);
//    for (Column relCol: rel.columns()) {
//      String alias = relCol.alias();
//      if (alias == null) {
//        alias = Strings.makeUnique(resolvedColumns.keySet(), "col", false);
//      }
//      Column col;
//      if (rel instanceof BaseRelation
//       || (rel instanceof AliasedRelation && ((AliasedRelation)rel).relation instanceof BaseRelation)) {
//        col = relCol.copy();
//        if (qualifier != null) {
//          ColumnRef.qualify(col.expression(), qualifier, null, true);
//        }
//        if (col.metadata() != null) {
//          for (Attribute attr: col.metadata().attributes().values()) {
//            ColumnRef.qualify(attr.attributeValue(), qualifier, null, true);
//          }
//        }
//      } else {
//        col = new Column(context,
//                         alias,
//                         new ColumnRef(context, qualifier, relCol.alias()),
//                         null);
//      }
//
//      int pos = alias.indexOf('/');
//      if (pos == -1) {
//        /*
//         * Normal column (not metadata).
//         */
//        if (resolvedColumns.containsKey(alias)) {
//          alias = Strings.makeUnique(resolvedColumns.keySet(), alias, false);
//        }
//        if (relCol.alias() != null && !alias.equals(relCol.alias())) {
//          aliased.put(relCol.alias(), alias);
//          col.alias(alias);
//        }
//        resolvedColumns.put(alias, col);
//
//      } else if (pos > 0) {
//        /*
//         * Column metadata
//         */
//        String columnName = alias.substring(0, pos);
//        if (aliased.containsKey(columnName)) {
//          // replace column name with replacement if the column name was changed
//          String aliasName = aliased.get(columnName);
//          alias = aliasName + alias.substring(pos);
//          col.alias(alias);
//        }
//        resolvedColumns.put(alias, col);
//
//      } else if (!resolvedColumns.containsKey(alias)) {
//        /*
//         * table metadata first encounter (by elimination, pos==0 in this case)
//         */
//        resolvedColumns.put(alias, col);
//      }
//    }
//    return new ArrayList<>(resolvedColumns.values());
//  }

//  private String alias(TableExpr from) {
//    if (from instanceof AbstractAliasTableExpr) {
//      return ((AbstractAliasTableExpr)from).alias();
//    } else if (from instanceof AbstractJoinTableExpr) {
//      return alias(((AbstractJoinTableExpr)from).left());
//    }
//    return null;
//  }

  @Override
  public String toString() {
    return (qualifier() == null ? "" : qualifier() + '.') + '*';
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(qualifier() == null ? "" : qualifier() + ".").append('*');
  }

  public String qualifier() {
    return childValue("qualifier");
  }
}