/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.variable;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.Result;
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
public class Assignment extends Expression<String, Assignment> {
  public Assignment(Context context,
                    String name,
                    Expression<?, ?> value) {
    super(context, "Assigment",
          T2.of("name",  new Esql<>(context, name)),
          T2.of("value", value));
  }

  public Assignment(Assignment other) {
    super(other);
  }

  @SafeVarargs
  public Assignment(Assignment other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Assignment copy() {
    return new Assignment(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public Assignment copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Assignment(this, value, children);
  }

  @Override
  public Assignment trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    return this;
  }

  @Override
  protected Object postTransformExec(EsqlConnection esqlCon,
                                     EsqlPath       path,
                                     Environment env) {
    if (!env.has(name())) {
      throw new ExecutionException("Unknown variable " + name() + " in " + env);
    }
    env.set(name(), value().exec(esqlCon, path.add(value()), env));
    return Result.Nothing;
  }

  public String name() {
    return childValue("name");
  }

  public Expression<?, ?> value() {
    return child("value");
  }
}
