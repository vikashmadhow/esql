/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

/**
 * Casts an expression to a given type.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Cast extends Expression<Type, String> {
  public Cast(Context context, Expression<?, ?> expr, Type toType) {
    super(context, toType, T2.of("expr", expr));
  }

  public Cast(Cast other) {
    super(other);
  }

  public Cast(Cast other, Type value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Cast copy() {
    return new Cast(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Cast copy(Type value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Cast(this, value, children);
  }

  @Override
  public Type type(EsqlPath path) {
    return toType();
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return switch (target) {
      case ESQL       -> toType().translate(target, path, parameters)
                           + '<' + expr().translate(target, path.add(expr()), parameters) + '>';
      case POSTGRESQL -> '(' + expr().translate(target, path.add(expr()), parameters) + ")::"
                             + toType().translate(target, path, parameters);
      case JSON,
           JAVASCRIPT -> expr().translate(target, path.add(expr()), parameters);    // ignore cast for Javascript
      default         -> "cast(" + expr().translate(target, path.add(expr()), parameters) + " as " + toType().translate(target, path, parameters) + ')';
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(toType().name()).append('<');
    expr()._toString(st, level, indent);
    st.append('>');
  }

  public Type toType() {
    return value;
  }

  public Expression<?, String> expr() {
    return child("expr");
  }
}
