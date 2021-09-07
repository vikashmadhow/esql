/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.SqlServer;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Restriction;
import ma.vi.esql.semantic.type.Selection;
import ma.vi.esql.syntax.expression.literal.NullLiteral;

import java.util.List;
import java.util.Map;

import static ma.vi.esql.syntax.Translatable.Target.HSQLDB;
import static ma.vi.esql.syntax.Translatable.Target.POSTGRESQL;

/**
 * A with query combines several common table expressions (query and updates) into an
 * arbitrarily complex query with individual CTEs able to refer to each other.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class With extends QueryUpdate {
  public With(Context context, boolean recursive, List<Cte> ctes, QueryUpdate query) {
    super(context, "With",
          T2.of("recursive", new Esql<>(context, recursive)),
          T2.of("ctes",      new Esql<>(context, ctes)),
          T2.of("query",     query),
          T2.of("columns",   new Esql<>(context,"columns", query.columns())),
          T2.of("tables",    query.tables()),
          T2.of("metadata",  query.metadata()));
  }

  public With(With other) {
    super(other);
  }

  public With(With other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public With copy() {
    return new With(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public With copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new With(this, value, children);
  }

  @Override
  public Selection type() {
    return query().type();
  }

  @Override
  public QueryTranslation trans(Target target, EsqlPath path, Map<String, Object> parameters) {
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
      st.append(cte.translate(target, path.add(cte), parameters).statement);
    }

    QueryTranslation q = query().translate(target, path.add(query()), parameters);
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
