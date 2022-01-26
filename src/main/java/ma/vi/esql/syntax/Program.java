/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.exec.Result;
import ma.vi.esql.semantic.scope.Allocator;
import ma.vi.esql.semantic.scope.FunctionScope;
import ma.vi.esql.semantic.scope.Scope;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.sql.Connection;
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
    Scope programScope = new FunctionScope("Program Scope", scope, new Allocator());
    super.scope(programScope, path);
    for (Expression<?, ?> e: expressions()) {
      e.scope(programScope, path.add(this));
    }
    return this;
  }

  @Override
  public List<?> trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    return expressions().stream()
                        .map(s -> s.translate(target, path.add(s), parameters))
                        .collect(toList());
  }

  @Override
  public Result execute(Database db, Connection con, EsqlPath path) {
    Result result = Result.Nothing;
    for (Expression<?, ?> st: expressions()) {
      Result r = st.execute(db, con, path.add(st));
      if (r != Result.Nothing) {
        result = r;
      }
    }
    return result;
  }

  public List<? extends Expression<?, ?>> expressions() {
    return children();
  }
}
