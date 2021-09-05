/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

/**
 * Name with associated metadata, used in dynamic tables definition.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NameWithMetadata extends TableDefinition {
  public NameWithMetadata(Context context, String name, Metadata metadata) {
    super(context, name, T2.of("metadata", metadata));
  }

  public NameWithMetadata(NameWithMetadata other) {
    super(other);
  }

  @Override
  public NameWithMetadata copy() {
    return new NameWithMetadata(this);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
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