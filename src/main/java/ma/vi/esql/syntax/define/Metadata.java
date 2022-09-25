/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.table.TableDefinition;
import org.pcollections.HashPMap;
import org.pcollections.IntTreePMap;
import org.pcollections.PMap;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static ma.vi.esql.database.Database.NULL_DB;
import static ma.vi.esql.database.EsqlConnection.NULL_CONNECTION;

/**
 * A list of attributes (name expression pairs) describing certain parts of
 * queries, tables, etc.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.mail)
 */
public class Metadata extends TableDefinition {
  public Metadata(Context context, List<Attribute> attributes) {
    super(context, "Metadata", attributes);
  }

  public Metadata(Metadata other) {
    super(other);
  }

  @SafeVarargs
  public Metadata(Metadata other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Metadata copy() {
    return new Metadata(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Metadata copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Metadata(this, value, children);
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    return attributes().values()
                       .stream()
                       .map(a -> a.translate(target, esqlCon, path.add(a), parameters, env))
                       .collect(joining(", ", "{", "}"));
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('{');
    boolean first = true;
    for (Attribute a: attributes().values()) {
      if (first) { first = false;   }
      else       { st.append(", "); }
      a._toString(st, level, indent);
    }
    st.append('}');
  }

  public Map<String, Attribute> attributes() {
    return childrenMap();
  }

  public Attribute attribute(String name) {
    return attributes().get(name);
  }

  public <T> T evaluateAttribute(String name) {
    return evaluateAttribute(name,
                             NULL_CONNECTION,
                             new EsqlPath(this),
                             HashPMap.empty(IntTreePMap.empty()),
                             NULL_DB.structure());
  }

  public <T> T evaluateAttribute(String               name,
                                 EsqlConnection       esqlCon,
                                 EsqlPath             path,
                                 PMap<String, Object> parameters,
                                 Environment          env) {
    Attribute a = attributes().get(name);
    return a == null ? null : a.evaluateAttribute(esqlCon, path, parameters, env);
  }
}