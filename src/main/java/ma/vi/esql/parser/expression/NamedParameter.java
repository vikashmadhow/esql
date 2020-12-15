/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

public class NamedParameter extends Expression<String> {
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
  public String translate(Target target) {
    return ':' + name();
  }

  public String name() {
    return value;
  }
}
