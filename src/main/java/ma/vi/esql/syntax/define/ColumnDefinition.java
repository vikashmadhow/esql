/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static ma.vi.esql.builder.Attributes.TYPE;
import static ma.vi.esql.translation.Translatable.Target.ESQL;
import static ma.vi.esql.translation.Translatable.Target.HSQLDB;

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
    super(context, "ColumnDef",
          T2.of("name", new Esql<>(context, name)),
          T2.of("type", new Esql<>(context, type)),
          T2.of("notNull", new Esql<>(context, notNull)),
          T2.of("expression", expression),
          T2.of("metadata", addType(context, metadata, type)));
  }

  public ColumnDefinition(ColumnDefinition other) {
    super(other);
  }

  @SafeVarargs
  public ColumnDefinition(ColumnDefinition other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public ColumnDefinition copy() {
    return new ColumnDefinition(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public ColumnDefinition copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new ColumnDefinition(this, value, children);
  }

  private static Metadata addType(Context context, Metadata metadata, Type type) {
    Map<String, Attribute> attrs = new LinkedHashMap<>();
    if (metadata != null) {
      attrs.putAll(metadata.attributes());
    }
    if (type != null) {
      attrs.put(TYPE, Attribute.from(context, TYPE, type.name()));
    }
    return new Metadata(context, new ArrayList<>(attrs.values()));
  }

  @Override
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    if (target == ESQL) {
      StringBuilder st = new StringBuilder('"' + name() + "\" "
                                               + type().translate(target, path, parameters)
                                               + (notNull() ? " not null" : "")
                                               + (expression() != null ? " default " + expression().translate(target, path.add(expression()), parameters) : ""));
      addMetadata(st, target);
      return st.toString();

    } else if (target== HSQLDB) {
      return '"' + name() + "\" "
          + type().translate(target, path, parameters)
          + (expression() != null ? " default " + expression().translate(target, path.add(expression()), parameters) : "")
          + (notNull() ? " not null" : "");

    } else {
      return '"' + name() + "\" "
           + type().translate(target, path, parameters)
           + (notNull() ? " not null" : "")
           + (expression() != null ? " default " + expression().translate(target, path.add(expression()), parameters) : "");
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

  public ColumnDefinition expression(Expression<?, String> expression) {
    return new ColumnDefinition(context, name(), type(), notNull(), expression, metadata());
  }

  public ColumnDefinition type(Type type) {
    return new ColumnDefinition(context, name(), type, notNull(), expression(), metadata());
  }

  public Expression<?, String> expression() {
    return child("expression");
  }

  public ColumnDefinition metadata(Metadata metadata) {
    return new ColumnDefinition(context, name(), type(), notNull(), expression(), metadata);
  }

  public Metadata metadata() {
    return child("metadata");
  }
}