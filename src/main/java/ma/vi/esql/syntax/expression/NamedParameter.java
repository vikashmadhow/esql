/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

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

  @SafeVarargs
  public NamedParameter(NamedParameter other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public NamedParameter copy() {
    return new NamedParameter(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public NamedParameter copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new NamedParameter(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
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
