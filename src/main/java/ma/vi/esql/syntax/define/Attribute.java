/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.literal.BooleanLiteral;
import ma.vi.esql.syntax.expression.literal.IntegerLiteral;
import ma.vi.esql.syntax.expression.literal.StringLiteral;
import ma.vi.esql.syntax.expression.literal.UuidLiteral;
import org.pcollections.PMap;

import java.util.UUID;

/**
 * An attribute is a named expression used as a unit of metadata.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Attribute extends Esql<String, String> {
  public Attribute(Context context,
                   String name,
                   Expression<?, String> value) {
    super(context, name, T2.of("value", value));
  }

  public Attribute(Attribute other) {
    super(other);
  }

  @SafeVarargs
  public Attribute(Attribute other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Attribute copy() {
    return new Attribute(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public Attribute copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Attribute(this, value, children);
  }

  public static Attribute from(Context context, String name, String value) {
    return new Attribute(context, name, new StringLiteral(context, value));
  }

  public static Attribute from(Context context, String name, boolean value) {
    return new Attribute(context, name, new BooleanLiteral(context, value));
  }

  public static Attribute from(Context context, String name, UUID value) {
    return new Attribute(context, name, new UuidLiteral(context, value));
  }

  public static Attribute from(Context context, String name, int value) {
    return new Attribute(context, name, new IntegerLiteral(context, (long)value));
  }

  @Override
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    return name() + ": " + (attributeValue() == null
                              ? "null"
                              : attributeValue().translate(target, path.add(attributeValue()), parameters));
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(name()).append(": ");
    if (attributeValue() == null) {
      st.append("null");
    } else {
      attributeValue()._toString(st, level, indent);
    }
  }

  public String name() {
    return value;
  }

  public Expression<?, String> attributeValue() {
    return child("value");
  }

  public Attribute attributeValue(Expression<?, String> value) {
    return set("value", value);
  }
}