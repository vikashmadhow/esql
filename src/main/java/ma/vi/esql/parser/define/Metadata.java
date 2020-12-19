/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.expression.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    super(context, null);
    attributes(attributes);
  }

  public Metadata(Metadata other) {
    super(other);
  }

  @Override
  public Metadata copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Metadata(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    return attributes().values()
                       .stream()
                       .map(a -> a.translate(target))
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
    return childValue("attributes");
  }

  public Metadata attributes(List<Attribute> attributes) {
    Map<String, Attribute> attributeMap = new HashMap<>();
    for (Attribute attr: attributes) {
      attributeMap.put(attr.name(), attr);
    }
    childValue("attributes", attributeMap);
    return this;
  }

  public Attribute attribute(String name) {
    return attributes().get(name);
  }

  public <T> T evaluateAttribute(String name) {
    Attribute a = attributes().get(name);
    if (a == null) {
      return null;
    } else {
      Expression<?> expr = a.attributeValue();
      return (T)expr.value(Target.ESQL);
    }
  }

  public Attribute attribute(String name, String value) {
    return attribute(name, new StringLiteral(context, value));
  }

  public Attribute attribute(String name, boolean value) {
    return attribute(name, new BooleanLiteral(context, value));
  }

  public Attribute attribute(String name, UUID value) {
    return attribute(name, new UuidLiteral(context, value));
  }

  public Attribute attribute(String name, int value) {
    return attribute(name, new IntegerLiteral(context, (long)value));
  }

  public Attribute attribute(String name, Expression<?> value) {
    Attribute a = new Attribute(context, name, value);
    attributes().put(name, a);
    return a;
  }
}