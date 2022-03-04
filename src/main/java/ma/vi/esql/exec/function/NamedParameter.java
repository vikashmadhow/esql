/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.literal.Literal;
import ma.vi.esql.translation.TranslationException;
import org.pcollections.PMap;

/**
 * A named parameter in ESQL consists of a name preceded with the '@' character.
 * Values for named parameters must be provided when a statement containing them
 * is executed.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NamedParameter extends Expression<String, String> {
  public NamedParameter(Context context, String name) {
    super(context, name);
  }

  public NamedParameter(NamedParameter other) {
    super(other);
  }

  @SafeVarargs
  public NamedParameter(NamedParameter other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public NamedParameter copy() {
    return new NamedParameter(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public NamedParameter copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new NamedParameter(this, value, children);
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
  protected Object postTransformExec(Target target,
                                     EsqlConnection esqlCon,
                                     EsqlPath path,
                                     PMap<String, Object> parameters, Environment env) {
    if (env.knows(name())) {
      return env.get(name());
    } else {
      throw new TranslationException(this, "A value for named parameter " + name()
                                        + " has not been provided.");
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('@').append(name());
  }

  public String name() {
    return value;
  }
}
