/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Selection;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Metadata;
import org.pcollections.PMap;

import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.HSQLDB;
import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;

/**
 * A with query combines several common table expressions (query and updates) into an
 * arbitrarily complex query with individual CTEs able to refer to each other.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class With extends QueryUpdate {
  public With(Context     context,
              boolean     recursive,
              List<Cte>   ctes,
              QueryUpdate query) {
    super(context, "With",
          T2.of("recursive", new Esql<>(context, recursive)),
          T2.of("ctes",      new Esql<>(context, "ctes", ctes)),
          T2.of("query",     query),
          T2.of("tables",    query.tables()),
          T2.of("columns",   new ColumnList(context, query.columns())),
          T2.of("metadata",  query.metadata()));
  }

  public With(With other) {
    super(other);
  }

  @SafeVarargs
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
  public TableExpr tables() {
    return query().tables();
  }

  @Override
  public Metadata metadata() {
    return query().metadata();
  }

  @Override
  public List<Column> columns() {
    return query().columns();
  }

  @Override
  public Selection computeType(EsqlPath path) {
    return (Selection)query().computeType(path.add(query()));
  }

  @Override
  public QueryTranslation trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    StringBuilder st = new StringBuilder("with ");
    if (recursive() && (target == POSTGRESQL || target == HSQLDB)) {
      st.append("recursive ");
    }

    boolean first = true;
    for (Cte cte: ctes()) {
      if (!first) st.append(", ");
      st.append(cte.translate(target, esqlCon, path.add(cte),
                              parameters.plus("recursive", first && recursive()),
                              env).translation());
      if (first) first = false;
    }

    QueryTranslation q = query().translate(target, esqlCon, path.add(query()), parameters, env);
    st.append(' ').append(q.translation());
    return new QueryTranslation(this, st.toString(),
                                q.columns(),
                                q.resultAttributes());
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

  public boolean recursive() {
    return childValue("recursive");
  }

  public List<Cte> ctes() {
    return child("ctes").children();
  }

  public QueryUpdate query() {
    return child("query");
  }
}
