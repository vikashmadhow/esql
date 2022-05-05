/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.conditional;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

/**
 * Breaks from the innermost loop.
 *
 * @author vikash.madhow@gmail.com
 */
public class Break extends Expression<String, Break> {
  public Break(Context context) {
    super(context, "Break");
  }

  public Break(Break other) {
    super(other);
  }

  @SafeVarargs
  public Break(Break other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Break copy() {
    return new Break(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Break copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Break(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.VoidType;
  }

  @Override
  protected Break trans(Target               target,
                        EsqlConnection       esqlCon,
                        EsqlPath             path,
                        PMap<String, Object> parameters,
                        Environment          env) {
    return this;
  }

  @Override
  public Object exec(Target target,
                     EsqlConnection esqlCon,
                     EsqlPath path,
                     PMap<String, Object> parameters, Environment env) {
    env.set("#break", true);
    return null;
  }
}