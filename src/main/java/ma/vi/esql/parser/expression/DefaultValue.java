/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

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

  @Override
  public DefaultValue copy() {
    if (!copying()) {
      try {
        copying(true);
        return new DefaultValue(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
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
  protected String trans(Target target, Map<String, Object> parameters) {
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
