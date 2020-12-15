/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.base.tuple.T2;

import static ma.vi.esql.parser.Translatable.Target.JSON;

public class NamedArgument extends Expression<String> {
  public NamedArgument(Context context, String name, Expression<?> arg) {
    super(context, name, T2.of("arg", arg));
  }

  public NamedArgument(NamedArgument other) {
    super(other);
  }

  @Override
  public NamedArgument copy() {
    if (!copying()) {
      try {
        copying(true);
        return new NamedArgument(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    switch (target) {
      case ESQL:
        return name() + ":=" + arg().translate(target);
      case JSON:
      case JAVASCRIPT:
        String translation = name() + '=' + arg().translate(target);
        return target == JSON ? '"' + translation + '"' : translation;
      default:
        // for databases drop name as it is not supported in most cases
        return arg().translate(target);
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(name()).append(":=");
    arg()._toString(st, level, indent);
  }

  public String name() {
    return value;
  }

  public Expression<?> arg() {
    return child("arg");
  }
}
