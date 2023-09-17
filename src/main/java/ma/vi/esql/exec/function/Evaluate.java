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
import ma.vi.esql.syntax.expression.literal.Literal;
import org.pcollections.PMap;

/**
 * Evaluates an expression in the form of `@(expression`)` and insert the result
 * in the enclosing ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Evaluate extends Expression<String, String> {
  public Evaluate(Context context, Expression<?, ?> expr) {
    super(context, "Evaluate", T2.of("expr", expr));
  }

  public Evaluate(Evaluate other) {
    super(other);
  }

  @SafeVarargs
  public Evaluate(Evaluate other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Evaluate copy() {
    return new Evaluate(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Evaluate copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Evaluate(this, value, children);
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    Object value = exec(target, esqlCon, path, parameters, env);
    return Literal.makeLiteral(context, value)
                  .translate(target, esqlCon, path, parameters, env);
  }

  @Override
  protected Object postTransformExec(Target               target,
                                     EsqlConnection       esqlCon,
                                     EsqlPath             path,
                                     PMap<String, Object> parameters,
                                     Environment          env) {
    return expr().exec(target, esqlCon, path, parameters, env);
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("@(");
    expr()._toString(st, level, indent);
    st.append(')');
  }

  public Expression<?, ?> expr() {
    return child("expr");
  }
}
