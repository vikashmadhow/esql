/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

public class Symbol extends Expression<String> {
  public Symbol(Context context, String name) {
    super(context, name);
  }

  public Symbol(Symbol other) {
    super(other);
  }

  @Override
  public Symbol copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Symbol(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    if (target == Target.ESQL) {
      return '`' + name();
    }
    /*
     * In all other cases, treat a symbol as a string.
     */
    return '\'' + name() + '\'';
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('`').append(name());
  }

  @Override
  public Object value(Target target) {
    return name();
  }

  public String name() {
    return value;
  }
}
