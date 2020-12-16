/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.type.Type;

import static ma.vi.esql.parser.Translatable.Target.ESQL;
import static ma.vi.esql.parser.Translatable.Target.HSQLDB;

public class ColumnDefinition extends TableDefinition {
  public ColumnDefinition(Context context,
                          String name,
                          Type type,
                          boolean notNull,
                          Expression<?> expression,
                          Metadata metadata) {
    super(context, name,
        T2.of("type",       new Esql<>(context, type)),
        T2.of("notNull",    new Esql<>(context, notNull)),
        T2.of("expression", expression),
        T2.of("metadata",   metadata));
  }

  public ColumnDefinition(ColumnDefinition other) {
    super(other);
  }

  @Override
  public ColumnDefinition copy() {
    if (!copying()) {
      try {
        copying(true);
        return new ColumnDefinition(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    if (target == ESQL) {
      StringBuilder st = new StringBuilder('"' + name() + "\" "
                                               + type().translate(target)
                                               + (notNull() ? " not null" : "")
                                               + (expression() != null ? " default " + expression().translate(target) : ""));
      addMetadata(st, target);
      return st.toString();

    } else if (target== HSQLDB) {
      return '"' + name() + "\" "
          + type().translate(target)
          + (expression() != null ? " default " + expression().translate(target) : "")
          + (notNull() ? " not null" : "");

    } else {
      return '"' + name() + "\" "
           + type().translate(target)
           + (notNull() ? " not null" : "")
           + (expression() != null ? " default " + expression().translate(target) : "");
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

  public Type type() {
    return childValue("type");
  }

  public Boolean notNull() {
    return childValue("notNull");
  }

  public Expression<?> expression() {
    return child("expression");
  }

  public Metadata metadata() {
    return child("metadata");
  }
}