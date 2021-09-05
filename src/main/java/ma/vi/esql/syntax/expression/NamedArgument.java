/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * A named argument to a function. The name is dropped when this is translated
 * to SQL as most databases do not support named arguments yet. This is
 * however useful for macro expansions.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NamedArgument extends Expression<String, String> {
  public NamedArgument(Context context, String name, Expression<?, String> arg) {
    super(context, name, T2.of("arg", arg));
  }

  public NamedArgument(NamedArgument other) {
    super(other);
  }

  @Override
  public NamedArgument copy() {
    return new NamedArgument(this);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    switch (target) {
      case ESQL:
        return name() + ":=" + arg().translate(target, path.add(arg()), parameters);
      case JSON:
      case JAVASCRIPT:
        String translation = name() + '=' + arg().translate(target, path.add(arg()), parameters);
        return target == JSON ? '"' + translation + '"' : translation;
      default:
        /*
         * for databases drop name as it is not supported in most cases
         */
        return arg().translate(target, path.add(arg()), parameters);
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

  public Expression<?, String> arg() {
    return child("arg");
  }
}
