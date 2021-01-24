/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;

import java.util.Map;

public class NameWithMetadata extends TableDefinition {
  public NameWithMetadata(Context context, String name, Metadata metadata) {
    super(context, name, T2.of("metadata", metadata));
  }

  public NameWithMetadata(NameWithMetadata other) {
    super(other);
  }

  @Override
  public NameWithMetadata copy() {
    if (!copying()) {
      try {
        copying(true);
        return new NameWithMetadata(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    String sql = '"' + name() + "\" ";
    if (target == Target.ESQL) {
      StringBuilder st = new StringBuilder(sql);
      addMetadata(st, target);
      return st.toString();
    } else {
      return sql;
    }
  }

  protected void addMetadata(StringBuilder st, Target target) {
    Metadata metadata = metadata();
    if (metadata != null && !metadata.attributes().isEmpty()) {
      st.append(" {");
      boolean first = true;
      for (Attribute a: metadata.attributes().values()) {
        if (first) {
          first = false;
        } else {
          st.append(", ");
        }
        st.append(a.name()).append(": ").append(a.attributeValue().translate(target));
      }
      st.append("}");
    }
  }

  public Metadata metadata() {
    return child("metadata");
  }
}