/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

/**
 * Represents the default value in insert and update statements.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DefaultValue extends Expression<String, String> {
  public DefaultValue(Context context) {
    super(context, "default");
  }

  public DefaultValue(DefaultValue other) {
    super(other);
  }

  public DefaultValue(DefaultValue other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public DefaultValue copy() {
    return new DefaultValue(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public DefaultValue copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new DefaultValue(this, value, children);
  }

  @Override
  public Type type() {
    /*
     * The type of the default value will require analysis of the
     * context, which is not necessary for the translation. TopType
     * is a placeholder for any type.
     */
    return Types.TopType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "default";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("default");
  }

  @Override
  public Object value(Target target) {
    return null;
  }
}
