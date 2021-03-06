/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.modify;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.query.AbstractJoinTableExpr;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.parser.query.SingleTableExpr;
import ma.vi.esql.parser.query.TableExpr;

import java.util.List;

import static ma.vi.base.tuple.T2.of;

/**
 * Update statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Update extends QueryUpdate {
  public Update(Context       context,
                String        updateTableAlias,
                TableExpr     from,
                Metadata      set,
                Expression<?> where,
                Metadata      returnMetadata,
                List<Column>  returnColumns) {
    super(context, "Update",
          of("updateTableAlias", new Esql<>(context, updateTableAlias)),
          of("set", set),
          of("tables", from),
          of("where", where),
          of("metadata", returnMetadata),
          of("columns", new Esql<>(context, returnColumns)));
  }

  public Update(Update other) {
    super(other);
  }

  @Override
  public Update copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Update(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public boolean modifying() {
    return true;
  }

  public static void addSet(StringBuilder st,
                            Metadata sets,
                            Target target,
                            boolean removeQualifier) {
    boolean first = true;
    st.append(" set ");
    for (Attribute set: sets.attributes().values()) {
      if (first) { first = false;   }
      else       { st.append(", "); }
      String columnName = set.name();
      if (removeQualifier) {
        int pos = columnName.lastIndexOf('.');
        if (pos != -1) {
          columnName = columnName.substring(pos + 1);
        }
      }
      st.append('"').append(columnName).append("\"=")
        .append(set.attributeValue().translate(target));
    }
  }

  /**
   * For postgresql updates, the table to be updated must be specified after the
   * `update` keyword and not repeated in the from table expression (unless for
   * a self-join). Since ESQL allows a single table expression containing the
   * whole join together with the table to update, the latter must be extracted.
   * This method finds the table to update (using its alias supplied in the
   * update statement), removes it from the whole table expression and returns
   * it, together with the parent join it belongs to. The parent join is used to
   * extract any condition set on the join and put it in the `where` clause.
   *
   * @param join The join to search for single update table.
   * @param singleTableAlias The alias of the single table being searched.
   * @return The single table to update together with the parent join it belongs
   *         to. The parent join is used to extract any condition set on the
   *         join and put it in the `where` clause of the query.
   */
  public static T2<AbstractJoinTableExpr, SingleTableExpr> removeSingleTable(AbstractJoinTableExpr join,
                                                                             String singleTableAlias) {
    if (join.left() instanceof SingleTableExpr
     && singleTableAlias.equals(((SingleTableExpr)join.left()).alias())) {
      /*
       *        J                    J
       *      /  \    remove x     /  \
       *     J    z  ---------->  y    z
       *    / \
       *   x  y
       */
      SingleTableExpr table = (SingleTableExpr)join.left();
      join.replaceWith(join.right());
      return T2.of(join, table);

    } else if (join.right() instanceof SingleTableExpr
            && singleTableAlias.equals(((SingleTableExpr)join.right()).alias())) {
      /*
       *        J                     J
       *      /  \   remove y       /  \
       *     J    z ---------->    x    z
       *    / \
       *   x  y
       */

      SingleTableExpr table = (SingleTableExpr)join.right();
      join.replaceWith(join.left());
      return T2.of(join, table);

    } else if (join.left() instanceof AbstractJoinTableExpr) {
      return removeSingleTable((AbstractJoinTableExpr)join.left(), singleTableAlias);

    } else if (join.right() instanceof AbstractJoinTableExpr) {
      return removeSingleTable((AbstractJoinTableExpr)join.right(), singleTableAlias);

    } else {
      return null;
    }
  }

  public Metadata set() {
    return child("set");
  }

  public String updateTableAlias() {
    return childValue("updateTableAlias");
  }
}