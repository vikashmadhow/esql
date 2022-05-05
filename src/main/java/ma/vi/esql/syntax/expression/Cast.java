/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

/**
 * Casts an expression to a given type.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Cast extends Expression<String, String> {
  public Cast(Context context, Expression<?, ?> expr, Type toType) {
    super(context, "Cast",
          T2.of("expr", expr),
          T2.of("type", new Esql<>(context, toType)));
  }

  public Cast(Cast other) {
    super(other);
  }

  @SafeVarargs
  public Cast(Cast other, String value, T2<String, ? extends Esql<?, ?>>... children) {
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
  public Cast copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Cast(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return toType();
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return switch (target) {
      case ESQL       -> toType().translate(target, esqlCon, path, parameters, env)
                           + '<' + expr().translate(target, esqlCon, path.add(expr()), parameters, env) + '>';
      case POSTGRESQL -> '(' + expr().translate(target, esqlCon, path.add(expr()), parameters, env) + ")::"
                             + toType().translate(target, esqlCon, path, parameters, env);
      case JSON,
           JAVASCRIPT -> expr().translate(target, esqlCon, path.add(expr()), parameters, env);    // ignore cast for Javascript
      default         -> "cast(" + expr().translate(target, esqlCon, path.add(expr()), parameters, env)
                       + " as " + toType().translate(target, esqlCon, path, parameters, env) + ')';
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(toType().name()).append('<');
    expr()._toString(st, level, indent);
    st.append('>');
  }

  public Type toType() {
    return childValue("type");
  }

  public Expression<?, String> expr() {
    return child("expr");
  }
}
