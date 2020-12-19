/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.modify;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.query.*;

import java.util.ArrayList;
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
  public Delete(Context context,
                SingleTableExpr from,
                TableExpr using,
                Expression<?> where,
                Metadata metadata, List<Column> returning) {
    super(context, "Delete",
          of("where", where),
          of("metadata", metadata),
          of("columns", new Esql<>(context, returning)));

    // combine using and from as the usage tables if both are specified
    if (using != null) {
      if (from.tableName() != null) {
        TableExpr expr = findTableExprWithAlias(using, from.alias());
        if (expr == null) {
          // using does not include from table: add as a cross-join to the tables of the delete
          child("tables", new CrossProductTableExpr(context, from, using));
        } else {
          // from table already included in using: ignore from all tables in delete
          child("tables", using);
        }
      } else {
        child("tables", using);
      }
    } else if (from != null && from.tableName() != null) {
      child("tables", from);
    } else {
      throw new TranslationException("No valid from or using clause specified for delete");
    }
    child("from", from);
    child("using", using);
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

    SingleTableExpr from = from();
    if (target == Target.SQLSERVER) {
      st.append(from.alias());

      // output clause for SQL Server if specified
      if (columns() != null) {
        st.append(" output ");
        q = constructResult(st, target, "deleted", true, true);
      }
      st.append(" from ").append(tables().translate(target));
      if (where() != null) {
        st.append(" where ").append(where().translate(target));
      }
    } else {
      // Postgresql
      TableExpr using = using();
      String mainAlias = from.alias();
      if (from.tableName() == null) {
        /*
         * Find table with this alias referred to in the list of
         * table expressions in the using clause.
         */
        TableExpr mainTable = findTableExprWithAlias(using, mainAlias);
        if (mainTable == null) {
          throw new TranslationException("Table with alias " + from.alias() + " not defined in the from or using clause");
        }
        st.append(" from ").append(mainTable.translate(target));

      } else {
        st.append(" from ").append(from.translate(target));
      }

      boolean whereAdded = false;
      if (using != null) {
        st.append(" using ");

        // assumes a linear list of joins as postgresql officially only supports a list in the using clause
        // (unofficially it also supports joins but it would be complicated to properly support this)
        List<TableExpr> linearisedTables = new ArrayList<>();
        List<Expression<?>> joinExpressions = new ArrayList<>();

        linearise(using, linearisedTables, joinExpressions);
        boolean first = true;
        for (TableExpr t: linearisedTables) {
          if (t instanceof AbstractAliasTableExpr) {
            AbstractAliasTableExpr aliased = (AbstractAliasTableExpr)t;
            if (aliased.alias().equals(mainAlias)) {
              // already added to from, ignore
              continue;
            }
          }
          if (first) {
            first = false;
          } else {
            st.append(", ");
          }
          st.append(t.translate(target));
        }
        for (Expression<?> expr: joinExpressions) {
          if (whereAdded) {
            st.append(" and ");
          } else {
            st.append(" where ");
            whereAdded = true;
          }
          st.append(expr.translate(target));
        }
      }
      if (where() != null) {
        if (whereAdded) {
          st.append(" and (");
        } else {
          st.append(" where (");
        }
        st.append(where().translate(target)).append(')');
      }
      if (columns() != null) {
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

  public static TableExpr findTableExprWithAlias(TableExpr expr, String alias) {
    if (expr instanceof SingleTableExpr && ((SingleTableExpr)expr).alias().equals(alias)) {
      return expr;

    } else if (expr instanceof SelectTableExpr && ((SelectTableExpr)expr).alias().equals(alias)) {
      return expr;

    } else if (expr instanceof AbstractJoinTableExpr) {
      AbstractJoinTableExpr join = (AbstractJoinTableExpr)expr;
      TableExpr e = findTableExprWithAlias(join.left(), alias);
      if (e == null) {
        e = findTableExprWithAlias(join.right(), alias);
      }
      return e;
    }
    return null;
  }

  public SingleTableExpr from() {
    return child("from");
  }

  public TableExpr using() {
    return child("using");
  }
}