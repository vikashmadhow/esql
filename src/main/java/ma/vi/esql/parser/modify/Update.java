/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.modify;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.query.*;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
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

  @Override
  public QueryTranslation translate(Target target) {
    StringBuilder st = new StringBuilder("update ");
    QueryTranslation q = null;

    TableExpr from = tables();
    if (from instanceof SingleTableExpr) {
      st. append(from.translate(target));

    } else if (from instanceof AbstractJoinTableExpr) {
      if (target == Target.SQLSERVER) {
        /*
         * SQL Server supports multiple tables in updates but, in those cases,
         * it is simpler and clearer to refer to the table being updated using
         * its alias instead of a double from clause.
         */
        st.append(updateTableAlias());
        addSet(st, set(), target, true);

        // output clause for SQL Server if specified
        if (columns() != null) {
          st.append(" output ");
          q = constructResult(st, target, "inserted", true, true);
        }
        st.append(" from ").append(from.translate(target));

      } else if (target == Target.POSTGRESQL) {
        /*
         * For postgresql the single table referred by the update table alias
         * must be extracted from the table expression and added as the main
         * table of the update statement; the rest of the table expression can
         * then be added to the `from` clause of the update; any join condition
         * removed when extracting the single update table must be moved to the
         * `where` clause.
         */
        T2<AbstractJoinTableExpr, SingleTableExpr> updateTable = removeSingleTable((AbstractJoinTableExpr)from,
                                                                                   updateTableAlias());
        if (updateTable == null) {
          throw new TranslationException("Could not find table with alias " + updateTableAlias());
        }
        if (updateTable.a instanceof JoinTableExpr) {
          JoinTableExpr join = (JoinTableExpr)updateTable.a;
          where(join.on(), false);
        }
        st.append(updateTable.b.translate(target));
        addSet(st, set(), target, false);
        st.append(" from ").append(tables().translate(target));

      } else if (target == Target.MARIADB) {
        /*
         * Maria DB allows simultaneous updates of multiple tables.
         */
        st.append(from.translate(target));
        addSet(st, set(), target, false);

      } else {
        throw new TranslationException(target + " does not support multiple tables or joins in updates");
      }
    } else {
      throw new TranslationException("Wrong table type to update: " + from);
    }

    if (where() != null) {
      st.append(" where ").append(where().translate(target));
    }
    if (columns() != null) {
      if (target == Target.MARIADB || target == Target.HSQLDB) {
        throw new TranslationException(target + " does not support return values in updates");

      } else if (target == Target.POSTGRESQL) {
        st.append(" returning ");
        q = constructResult(st, target, null, true, true);
      }
    }
    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                  emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                  q.resultAttributeIndices, q.resultAttributes);
    }
  }

  private static void addSet(StringBuilder st,
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
  protected static T2<AbstractJoinTableExpr, SingleTableExpr> removeSingleTable(AbstractJoinTableExpr join,
                                                                                String singleTableAlias) {
    if (join.left() instanceof SingleTableExpr
     && singleTableAlias.equals(((SingleTableExpr)join.left()).alias())) {
      /*
       *        J                        J
       *      /   \     remove x       /   \
       *     J    z    ---------->    y     z
       *    / \
       *   x  y
       */
      SingleTableExpr table = (SingleTableExpr)join.left();
      join.replaceWith(join.right());
      return T2.of(join, table);

    } else if (join.right() instanceof SingleTableExpr
            && singleTableAlias.equals(((SingleTableExpr)join.right()).alias())) {
      /*
       *        J                        J
       *      /   \     remove y       /   \
       *     J    z    ---------->    x     z
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