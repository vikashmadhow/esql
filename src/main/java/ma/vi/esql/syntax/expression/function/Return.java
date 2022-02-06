/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.function;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

/**
 * A variable declaration in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Return extends Expression<String, Return> {
  public Return(Context context, Expression<?, ?> value) {
    super(context, "Return", T2.of("value", value));
  }

  public Return(Return other) {
    super(other);
  }

  @SafeVarargs
  public Return(Return other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Return copy() {
    return new Return(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public Return copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Return(this, value, children);
  }

  @Override
  public Return trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return this;
  }

  @Override
  public Object exec(Target target, EsqlConnection esqlCon,
                     EsqlPath path,
                     Environment env) {
    return value().exec(target, esqlCon, path.add(value()), env);
  }

  public Expression<?, ?> value() {
    return child("value");
  }
}
