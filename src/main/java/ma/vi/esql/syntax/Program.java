/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Return;
import ma.vi.esql.semantic.scope.FunctionScope;
import ma.vi.esql.semantic.scope.Scope;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * A sequence of ESQL statements is a program.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Program extends Esql<String, List<?>> {
  public Program(Context context, List<? extends Expression<?, ?>> expressions) {
    super(context, "Program", expressions, true);
  }

  public Program(Program other) {
    super(other);
  }

  @SafeVarargs
  public Program(Program other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Program copy() {
    return new Program(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  public Program copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Program(this, value, children);
  }

  @Override
  public Esql<?, ?> scope(Scope scope, EsqlPath path) {
    Scope programScope = new FunctionScope(scope);
    super.scope(programScope, path);
    for (Expression<?, ?> e: expressions()) {
      e.scope(programScope, path.add(this));
    }
    return this;
  }

  @Override
  public List<?> trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return expressions().stream()
                        .map(s -> s.translate(target, esqlCon, path.add(s), parameters, env))
                        .collect(toList());
  }

  @Override
  public Object exec(Target target, EsqlConnection esqlCon,
                     EsqlPath path,
                     PMap<String, Object> parameters, Environment env) {
    Object ret = Result.Nothing;
    for (Expression<?, ?> st: expressions()) {
      ret = st.exec(target, esqlCon, path.add(st), parameters, env);
      if (st instanceof Return) {
        return ret;
      }
    }
    return ret;
  }

  public List<? extends Expression<?, ?>> expressions() {
    return children();
  }
}
