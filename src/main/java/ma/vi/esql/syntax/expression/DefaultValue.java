/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

/**
 * Represents the default value in insert and update statements.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DefaultValue extends Expression<String, String> {
  public DefaultValue(Context context) {
    super(context, "Default");
  }

  public DefaultValue(DefaultValue other) {
    super(other);
  }

  @SafeVarargs
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
  public Type computeType(EsqlPath path) {
    /*
     * The type of the default value will require analysis of the
     * context, which is not necessary for the translation. TopType
     * is a placeholder for any type.
     */
    return Types.TopType;
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return "default";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("default");
  }

  @Override
  public Object exec(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return null;
  }
}
