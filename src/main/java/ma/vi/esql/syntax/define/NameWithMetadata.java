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
import org.pcollections.PMap;

/**
 * Name with associated metadata, used in dynamic tables definition.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NameWithMetadata extends TableDefinition {
  public NameWithMetadata(Context context, String name, Metadata metadata) {
    super(context, "NameWithMetadata",
          T2.of("name", new Esql<>(context, name)),
          T2.of("metadata", metadata));
  }

  public NameWithMetadata(NameWithMetadata other) {
    super(other);
  }

  @SafeVarargs
  public NameWithMetadata(NameWithMetadata other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public NameWithMetadata copy() {
    return new NameWithMetadata(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public NameWithMetadata copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new NameWithMetadata(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
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

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(name());
    if (metadata() != null
     && metadata().attributes() != null
     && !metadata().attributes().isEmpty()) {
      metadata()._toString(st, level + 1, indent);
    }
  }

  public Metadata metadata() {
    return child("metadata");
  }
}