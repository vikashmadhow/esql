/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.type.Type;

import java.util.Map;

import static ma.vi.esql.syntax.Translatable.Target.ESQL;
import static ma.vi.esql.syntax.Translatable.Target.HSQLDB;

/**
 * The definition of a column in a create or alter table statement.
 *
 * @author vikash.madhow@gmail.com
 */
public class ColumnDefinition extends TableDefinition {
  public ColumnDefinition(Context context,
                          String name,
                          Type type,
                          boolean notNull,
                          Expression<?, String> expression,
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
  protected String trans(Target target, Map<String, Object> parameters) {
    if (target == ESQL) {
      StringBuilder st = new StringBuilder('"' + name() + "\" "
                                               + type().translate(target, parameters)
                                               + (notNull() ? " not null" : "")
                                               + (expression() != null ? " default " + expression().translate(target, parameters) : ""));
      addMetadata(st, target);
      return st.toString();

    } else if (target== HSQLDB) {
      return '"' + name() + "\" "
          + type().translate(target, parameters)
          + (expression() != null ? " default " + expression().translate(target, parameters) : "")
          + (notNull() ? " not null" : "");

    } else {
      return '"' + name() + "\" "
           + type().translate(target, parameters)
           + (notNull() ? " not null" : "")
           + (expression() != null ? " default " + expression().translate(target, parameters) : "");
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

  public Expression<?, String> expression() {
    return child("expression");
  }

  public Metadata metadata() {
    return child("metadata");
  }
}