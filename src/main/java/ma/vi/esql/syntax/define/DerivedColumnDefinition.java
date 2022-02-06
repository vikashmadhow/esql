/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static ma.vi.esql.builder.Attributes.DERIVED;

/**
 * The definition of a derived column in a create table statement.
 *
 * @author vikash.madhow@gmail.com
 */
public class DerivedColumnDefinition extends ColumnDefinition {
  public DerivedColumnDefinition(Context context,
                                 String name,
                                 Expression<?, String> expression,
                                 Metadata metadata) {
    this(context, name, null, expression, addDerived(context, metadata));
  }

  private DerivedColumnDefinition(Context context,
                                  String name,
                                  Type type,
                                  Expression<?, String> expression,
                                  Metadata metadata) {
    super(context, name, type, false, expression, addDerived(context, metadata));
  }

  public DerivedColumnDefinition(DerivedColumnDefinition other) {
    super(other);
  }

  @SafeVarargs
  public DerivedColumnDefinition(DerivedColumnDefinition other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public DerivedColumnDefinition copy() {
    return new DerivedColumnDefinition(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public DerivedColumnDefinition copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new DerivedColumnDefinition(this, value, children);
  }

  private static Metadata addDerived(Context context, Metadata metadata) {
    Map<String, Attribute> attrs = new LinkedHashMap<>();
    if (metadata != null) {
      attrs.putAll(metadata.attributes());
    }
    attrs.put(DERIVED, Attribute.from(context, DERIVED, true));
    return new Metadata(context, new ArrayList<>(attrs.values()));
  }

  @Override
  public DerivedColumnDefinition expression(Expression<?, String> expression) {
    return new DerivedColumnDefinition(context, name(), expression, metadata());
  }

  @Override
  public DerivedColumnDefinition metadata(Metadata metadata) {
    return new DerivedColumnDefinition(context, name(), expression(), metadata);
  }

  @Override
  public DerivedColumnDefinition type(Type type) {
    return new DerivedColumnDefinition(context, name(), type, expression(), metadata());
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    if (target == Target.ESQL) {
      StringBuilder st = new StringBuilder("derived \"" + name() + "\" " + expression().translate(target,
                                                                                                  esqlCon,
                                                                                                  path.add(expression()),
                                                                                                  parameters,
                                                                                                  env));
      addMetadata(st, target);
      return st.toString();
    }
    /*
     * Derived expressions don't produce SQL statements directly; they are just
     * inserted into the table definitions of the core schema.
     */
    return null;
  }
}
