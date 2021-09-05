/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;

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
//    Map<String, Attribute> attributeMap = new HashMap<>();
//    for (Attribute attr: attributes) {
//      if (attr != null) {
//        attr.parent = this;
//        attributeMap.put(attr.name(), attr);
//      }
//    }
//    childValue("attributes", attributeMap);
  }

  public Metadata(Metadata other) {
    super(other);
  }

  @Override
  public Metadata copy() {
    return new Metadata(this);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
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
      return (T)expr.value(Target.ESQL);
    }
  }
}