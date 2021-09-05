/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;

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
    return new NamedParameter(this);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
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
