/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.SqlServer;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.QueryUpdate;
import ma.vi.esql.parser.Restriction;

import java.util.List;
import java.util.Map;

import static ma.vi.base.tuple.T2.of;
import static ma.vi.esql.parser.Translatable.Target.HSQLDB;
import static ma.vi.esql.parser.Translatable.Target.POSTGRESQL;

/**
 * A with query combines several common table expressions (query and updates) into an
 * arbitrarily complex query with individual CTEs able to refer to each other.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class With extends QueryUpdate {
  public With(Context context, boolean recursive, List<Cte> ctes, QueryUpdate query) {
    super(context, "With",
        of("recursive", new Esql<>(context, recursive)),
        of("ctes", new Esql<>(context, ctes)),
        of("query", query));

    child("columns", query.columnsAsEsql(), false);
    child("tables", query.tables(), false);
    child("metadata", query.metadata(), false);
  }

  public With(With other) {
    super(other);
  }

  @Override
  public With copy() {
    if (!copying()) {
      try {
        copying(true);
        return new With(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public QueryTranslation translate(Target target, Map<String, Object> parameters) {
    /*
     * Ensure all CTE types are added to context-specific (local) type registry.
     */
    for (Cte cte: ctes()) {
      cte.type();
    }

    StringBuilder st = new StringBuilder("with ");
    if (recursive() && (target == POSTGRESQL || target == HSQLDB)) {
      st.append("recursive ");
    }

    boolean first = true;
    for (Cte cte: ctes()) {
      if (first) { first = false; }
      else       { st.append(", "); }
      st.append(cte.translate(target, parameters).statement);
    }

    QueryTranslation q = query().translate(target, parameters);
    st.append(' ').append(q.statement);
    return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
        q.resultAttributeIndices, q.resultAttributes);
  }

  @Override
  public boolean modifying() {
    for (Cte cte: ctes()) {
      if (cte.modifying()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public T2<Boolean, String> restrict(Restriction restriction,
                                      String targetAlias,
                                      boolean ignoreHiddenFields,
                                      boolean followSubSelect) {
    boolean joined = false;
    String alias = null;
    boolean first = true;
    for (Cte cte: ctes()) {
      /*
       * For recursive with query, the first CTE does not support distinct for
       * SQL SERVER; thus for that part of the WITH query, disable the rewriting.
       */
      if (!(context.structure.database instanceof SqlServer)
          || !recursive()
          || !first) {
        T2<Boolean, String> res = cte.restrict(restriction, targetAlias, ignoreHiddenFields, followSubSelect);
        joined |= res.a;
        alias = res.b == null ? alias : res.b;
      }
      first = false;
    }
    T2<Boolean, String> res = query().restrict(restriction, targetAlias, ignoreHiddenFields, followSubSelect);
    joined |= res.a;
    alias = res.b == null ? alias : res.b;
    return T2.of(joined, alias);
  }

  public boolean recursive() {
    return childValue("recursive");
  }

  public List<Cte> ctes() {
    return childValue("ctes");
  }

  public QueryUpdate query() {
    return child("query");
  }
}
