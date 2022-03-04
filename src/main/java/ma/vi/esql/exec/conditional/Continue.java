/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.conditional;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

/**
 * Move to the next iteration of the loop.
 *
 * @author vikash.madhow@gmail.com
 */
public class Continue extends Expression<String, Continue> {
  public Continue(Context context) {
    super(context, "Continue");
  }

  public Continue(Continue other) {
    super(other);
  }

  @SafeVarargs
  public Continue(Continue other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Continue copy() {
    return new Continue(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Continue copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Continue(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.VoidType;
  }

  @Override
  protected Continue trans(Target               target,
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
    env.set("#continue", true);
    return null;
  }
}