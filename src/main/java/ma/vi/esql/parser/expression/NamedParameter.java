/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import java.util.Map;

/**
 * A named parameter in ESQL consists of a name preceded with a colon (:).
 * Values for named parameters must be provided when the statement is executed.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NamedParameter extends Expression<String, String> {
  public NamedParameter(Context context, String name) {
    super(context, name);
  }

  public NamedParameter(NamedParameter other) {
    super(other);
  }

  @Override
  public NamedParameter copy() {
    if (!copying()) {
      try {
        copying(true);
        return new NamedParameter(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    return ':' + name();
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(':').append(name());
  }

  public String name() {
    return value;
  }
}
