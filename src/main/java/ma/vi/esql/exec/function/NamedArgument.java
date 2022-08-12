/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

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

  @SafeVarargs
  public NamedArgument(NamedArgument other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public NamedArgument copy() {
    return new NamedArgument(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public NamedArgument copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new NamedArgument(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return switch (target) {
      case ESQL       -> name() + "="  + arg().translate(target, esqlCon, path.add(arg()), parameters, env);
      case JAVASCRIPT -> name() + ':'  + arg().translate(target, esqlCon, path.add(arg()), parameters, env);
      default ->
        /*
         * for databases drop name as it is not supported in most cases
         */
        arg().translate(target, esqlCon, path.add(arg()), parameters, env);
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(name()).append("=");
    arg()._toString(st, level, indent);
  }

  public String name() {
    return value;
  }

  public Expression<?, String> arg() {
    return child("arg");
  }
}
