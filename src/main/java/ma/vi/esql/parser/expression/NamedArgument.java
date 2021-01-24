/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;

import java.util.Map;

import static ma.vi.esql.parser.Translatable.Target.JSON;

/**
 * A named argument to a function. The name is dropped when this is translated
 * to SQL as most databases do not support named arguments yet. This is
 * however useful for macro expansions.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
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
  public String translate(Target target, Map<String, Object> parameters) {
    switch (target) {
      case ESQL:
        return name() + ":=" + arg().translate(target, parameters);
      case JSON:
      case JAVASCRIPT:
        String translation = name() + '=' + arg().translate(target, parameters);
        return target == JSON ? '"' + translation + '"' : translation;
      default:
        /*
         * for databases drop name as it is not supported in most cases
         */
        return arg().translate(target, parameters);
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
