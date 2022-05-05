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
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.literal.BooleanLiteral;
import ma.vi.esql.syntax.expression.literal.IntegerLiteral;
import ma.vi.esql.syntax.expression.literal.StringLiteral;
import ma.vi.esql.syntax.expression.literal.UuidLiteral;
import org.pcollections.HashPMap;
import org.pcollections.IntTreePMap;
import org.pcollections.PMap;

import java.util.UUID;
import java.util.regex.Pattern;

import static ma.vi.esql.database.Database.NULL_DB;
import static ma.vi.esql.database.EsqlConnection.NULL_CONNECTION;

/**
 * An attribute is a named expression used as a unit of metadata.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Attribute extends Esql<String, String> {
  public Attribute(Context          context,
                   String           name,
                   Expression<?, ?> value) {
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
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
//    String name = name();
//    if (!IDENTIFIER.matcher(name).matches()) {
//      name = '"' + name + '"';
//    }
    return '"' + name() + "\": " + (attributeValue() == null
                                 ? "null"
                                 :  attributeValue().translate(target, esqlCon, path.add(attributeValue()), parameters, env));
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

  public Expression<?, ?> attributeValue() {
    return child("value");
  }

  public Attribute attributeValue(Expression<?, ?> value) {
    return set("value", value);
  }

  public <T> T evaluateAttribute() {
    return evaluateAttribute(NULL_CONNECTION,
                             new EsqlPath(this),
                             HashPMap.empty(IntTreePMap.empty()),
                             NULL_DB.structure());
  }

  public <T> T evaluateAttribute(EsqlConnection       esqlCon,
                                 EsqlPath             path,
                                 PMap<String, Object> parameters,
                                 Environment          env) {
    Expression<?, ?> expr = attributeValue();
    return (T)expr.exec(Target.ESQL,
                        esqlCon,
                        path == null ? new EsqlPath(expr) : path.add(expr),
                        parameters, env);
  }

  public static final Pattern IDENTIFIER = Pattern.compile("[$_a-zA-Z][$_a-zA-Z0-9]*");
}