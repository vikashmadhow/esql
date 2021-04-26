/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * The wild-card (*) representing all columns in a query.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class StarColumn extends Column { // implements Macro {
  public StarColumn(Context context, String qualifier) {
    super(context, "_", null, null);
    child("qualifier", new Esql<>(context, qualifier));
  }

  public StarColumn(StarColumn other) {
    super(other);
  }

  @Override
  public StarColumn copy() {
    if (!copying()) {
      try {
        copying(true);
        return new StarColumn(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

//  @Override
//  public int expansionOrder() {
//    return HIGHEST;
//  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
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

//  /**
//   * Expands star columns (*) to the individual columns of the referred table
//   * if it is not a parameter to a function.
//   */
//  @Override
//  public boolean expand(String name, Esql<?, ?> esql) {
//    int queryDistance = ancestorDistance(QueryUpdate.class);
//    int functionDistance = ancestorDistance(FunctionCall.class);
//
//    if (functionDistance == -1 || functionDistance > queryDistance) {
//      QueryUpdate stmt = ancestor(QueryUpdate.class);
//      if (stmt == null) {
//        throw new TranslationException("All columns reference (*) must be in a select, insert, update " +
//            "or delete statement.");
//      }
//      TableExpr from = stmt.tables();
//      BaseRelation relation = from.type();
//      List<Column> columns = stmt.columns();
//      int i = columns.indexOf(parent);
//      if (i == -1) {
//        throw new TranslationException("Could not find column corresponding to an all-columns (*) reference " +
//            "in the parent query (" + stmt + ")");
//      }
//      String qualifier = qualifier();
//      BaseRelation referred = qualifier == null ? relation : relation.forAlias(qualifier);
//      if (referred == null) {
//        throw new TranslationException("An all-columns reference (*) refers to a table aliased as " + qualifier +
//            "; the table with this alias cannot be found.");
//      }
//
//      columns.remove(i);
//      for (Field field: referred.orderedFields()) {
//        String fieldName = field.name();
//        List<Attribute> attributes = new ArrayList<>();
//        // String alias = qualifier != null ? qualifier : alias(from);
//        String alias = null;
//        if (field.relation() != null) {
//          alias = relation.aliasFor(field.relation());
//        }
//        if (alias == null) {
//          alias = qualifier;
//        }
//        for (Map.Entry<String, DbExpression> e: field.attributes().entrySet()) {
//          attributes.add(new Attribute(context, e.getKey(), e.getValue().expression()));
//        }
//        Column col;
//        if (field.typeOfField() == PERSISTENT || field.typeOfField() == DERIVED) {
//          col = new Column(context,
//              null, new ColumnRef(context, alias, fieldName),
//              new Metadata(context, attributes.isEmpty() ? emptyList() : attributes));
//        } else {
//          /*
//           * Transient: column may be an expression: qualify if necessary
//           */
//          Expression<?, String> colExpr = field.expression().expression();
////                    if (qualifier != null) {
////                        colExpr = ColumnRef.qualify(colExpr, qualifier, false);
////                    }
//          colExpr = ColumnRef.qualify(colExpr, alias, null, false);
//          col = new Column(context, fieldName, colExpr,
//              new Metadata(context, attributes.isEmpty() ? emptyList() : attributes));
//        }
//        col.parent = stmt;
//        columns.add(i, col);
//        i++;
//      }
//      return true;
//    }
//    return false;
//  }
//
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