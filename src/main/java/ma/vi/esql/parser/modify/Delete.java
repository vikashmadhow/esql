/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.modify;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.query.*;
import ma.vi.esql.type.Type;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static ma.vi.base.tuple.T2.of;

/**
 * Delete statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Delete extends QueryUpdate {
  public Delete(Context       context,
                String        deleteTableAlias,
                TableExpr     from,
                Expression<?> where,
                Metadata      returnMetadata,
                List<Column>  returnColumns) {
    super(context, "Delete",
          of("deleteTableAlias", new Esql<>(context, deleteTableAlias)),
          of("tables", from),
          of("where", where),
          of("metadata", returnMetadata),
          of("columns", new Esql<>(context, returnColumns)));
  }

  public Delete(Delete other) {
    super(other);
  }

  @Override
  public Delete copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Delete(this);
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
    StringBuilder st = new StringBuilder("delete ");
    QueryTranslation q = null;

    TableExpr from = tables();
    if (from instanceof SingleTableExpr) {
      st.append(" from ").append(from.translate(target));

    } else if (from instanceof AbstractJoinTableExpr) {
      if (target == Target.SQLSERVER) {
        /*
         * SQL Server supports multiple tables in deletes with similar reference
         * to the target table through its alias.
         */
        st.append(deleteTableAlias());

        // output clause for SQL Server if specified
        if (columns() != null) {
          st.append(" output ");
          q = constructResult(st, target, "deleted", true, true);
        }
        st.append(" from ").append(from.translate(target));

      } else if (target == Target.POSTGRESQL) {
        /*
         * For postgresql the single table referred by the delete table alias
         * must be extracted from the table expression and added as the main
         * table of the update statement; the rest of the table expression can
         * then be added to the `using` clause of the delete; any join condition
         * removed when extracting the single update table must be moved to the
         * `where` clause.
         */
        T2<AbstractJoinTableExpr, SingleTableExpr> updateTable = Update.removeSingleTable((AbstractJoinTableExpr)from,
                                                                                          deleteTableAlias());
        if (updateTable == null) {
          throw new TranslationException("Could not find table with alias " + deleteTableAlias());
        }
        if (updateTable.a instanceof JoinTableExpr) {
          JoinTableExpr join = (JoinTableExpr)updateTable.a;
          where(join.on(), false);
        }
        st.append(" from ").append(updateTable.b.translate(target));
        st.append(" using ").append(from.translate(target));

      } else if (target == Target.MARIADB) {
        /*
         * Maria DB allows a form of delete using joins which is close to ESQL
         * but uses the table name instead of the alias.
         */
        SingleTableExpr deleteTable = findSingleTable((AbstractJoinTableExpr)from, deleteTableAlias());
        if (deleteTable == null) {
          throw new TranslationException("Could not find table with alias " + deleteTableAlias());
        }
        st.append(Type.dbName(deleteTable.tableName(), target));
        st.append(" from ").append(from.translate(target));

      } else {
        throw new TranslationException(target + " does not support multiple tables or joins in deletes");
      }
    } else {
      throw new TranslationException("Wrong table type to delete: " + from);
    }

    if (where() != null) {
      st.append(" where ").append(where().translate(target));
    }
    if ((target == Target.POSTGRESQL || target == Target.MARIADB) && columns() != null) {
      st.append(" returning ");
      q = constructResult(st, target, null, true, true);
    }
    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                  emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                  q.resultAttributeIndices, q.resultAttributes);
    }
  }

  public String deleteTableAlias() {
    return childValue("deleteTableAlias");
  }

  protected static SingleTableExpr findSingleTable(AbstractJoinTableExpr join,
                                                   String alias) {
    if (join.left() instanceof SingleTableExpr
     && alias.equals(((SingleTableExpr)join.left()).alias())) {
      return (SingleTableExpr)join.left();

    } else if (join.right() instanceof SingleTableExpr
            && alias.equals(((SingleTableExpr)join.right()).alias())) {
      return (SingleTableExpr)join.right();

    } else if (join.left() instanceof AbstractJoinTableExpr) {
      return findSingleTable((AbstractJoinTableExpr)join.left(), alias);

    } else if (join.right() instanceof AbstractJoinTableExpr) {
      return findSingleTable((AbstractJoinTableExpr)join.right(), alias);

    } else {
      return null;
    }
  }
}