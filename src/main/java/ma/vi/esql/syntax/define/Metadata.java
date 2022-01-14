/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * A list of attributes (name expression pairs) describing
 * certain parts of queries, tables, etc. This is also used as
 * the update set clause as it has the same structure.
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
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    return attributes().values()
                       .stream()
                       .map(a -> a.translate(target, path.add(a), parameters))
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
    Attribute a = attributes().get(name);
    if (a == null) {
      return null;
    } else {
      Expression<?, String> expr = a.attributeValue();
      return (T)expr.value(Target.ESQL, new EsqlPath(expr));
    }
  }
}