/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.function;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.scope.Symbol;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

/**
 * Parameter in function declaration.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class FunctionParameter extends Esql<String, String> implements Symbol {
  public FunctionParameter(Context          context,
                           String           name,
                           Type             type,
                           Expression<?, ?> defaultValue) {
    super(context, name,
          T2.of("type", new Esql<>(context, type)),
          T2.of("defaultValue", defaultValue));
    this.type = type;
  }

  public FunctionParameter(FunctionParameter other) {
    super(other);
  }

  @SafeVarargs
  public FunctionParameter(FunctionParameter other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public FunctionParameter copy() {
    return new FunctionParameter(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public FunctionParameter copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new FunctionParameter(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    return switch (target) {
      case ESQL -> name() + " : " + type().translate(target, path, parameters);
      case JSON, JAVASCRIPT -> name();
      default ->
        /*
         * for databases drop name as it is not supported in most cases
         */
        type().translate(target, path, parameters) + ' ' + name();
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(name()).append(" : ").append(type().name());
  }

  @Override
  public String name() {
    return value;
  }

  @Override
  public Type type() {
    return childValue("type");
  }

  public Expression<?, ?> defaultValue() {
    return child("defaultValue");
  }
}
