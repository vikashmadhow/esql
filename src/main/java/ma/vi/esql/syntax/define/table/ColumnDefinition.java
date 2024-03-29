/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define.table;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.translation.Translatable;
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
  public ColumnDefinition(Context          context,
                          String           name,
                          Type             type,
                          boolean          notNull,
                          Expression<?, ?> expression,
                          Metadata metadata) {
    super(context, "ColumnDef",
          T2.of("name",       new Esql<>(context, name)),
          T2.of("type",       new Esql<>(context, type)),
          T2.of("notNull",    new Esql<>(context, notNull)),
          T2.of("expression", expression),
          T2.of("metadata",   addType(context, metadata, type)));
    this.type = type;
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
  protected String trans(Translatable.Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    if (target == ESQL) {
      StringBuilder st = new StringBuilder('"' + name() + "\" "
                                               + type().translate(target, esqlCon, path, parameters, env)
                                               + (notNull() ? " not null" : "")
                                               + (expression() != null ? " default " + expression().translate(target,
                                                                                                              esqlCon,
                                                                                                              path.add(expression()),
                                                                                                              parameters,
                                                                                                              env) : ""));
      addMetadata(st, target);
      return st.toString();

    } else if (target== HSQLDB) {
      return '"' + name() + "\" "
          + type().translate(target, esqlCon, path, parameters, env)
          + (expression() != null ? " default " + expression().translate(target, esqlCon, path.add(expression()), parameters, env) : "")
          + (notNull() ? " not null" : "");

    } else {
      return '"' + name() + "\" "
           + type().translate(target, esqlCon, path, parameters, env)
           + (notNull() ? " not null" : "")
           + (expression() != null ? " default " + expression().translate(target, esqlCon, path.add(expression()), parameters, env) : "");
    }
  }

  protected void addMetadata(StringBuilder st, Translatable.Target target) {
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
  public Type type() {
    return childValue("type");
  }

  @Override
  public ColumnDefinition type(Type type) {
    return new ColumnDefinition(context, name(), type, notNull(), expression(), metadata());
  }

  public Boolean notNull() {
    return childValue("notNull");
  }

  public ColumnDefinition expression(Expression<?, String> expression) {
    return new ColumnDefinition(context, name(), type(), notNull(), expression, metadata());
  }

  public Expression<?, ?> expression() {
    return child("expression");
  }

  public ColumnDefinition metadata(Metadata metadata) {
    return new ColumnDefinition(context, name(), type(), notNull(), expression(), metadata);
  }

  public Metadata metadata() {
    return child("metadata");
  }

  /**
   * A sequence number for the column which, if set, will be used as its sequence
   * in the _core.columns table. This is set by CreateTable when adding a new
   * column to an existing table so that the column does not have the default
   * sequence (at the end of all previous columns in the table) but instead is
   * assigned a sequence based on its position in the `create table` statement.
   */
  public int seq = -1;
}