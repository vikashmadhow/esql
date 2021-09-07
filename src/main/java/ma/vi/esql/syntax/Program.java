/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.exec.Result;
import ma.vi.esql.syntax.expression.Expression;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * A sequence of ESQL statements is a program.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Program extends Esql<String, List<?>> {
  public Program(Context context, List<Expression<?, ?>> expressions) {
    super(context, "program", expressions);
  }

  public Program(Program other) {
    super(other);
  }

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
  public List<?> trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return expressions().stream()
                        .map(s -> s.translate(target, path.add(s), parameters))
                        .collect(toList());
  }

  @Override
  public Result execute(Database database, Connection connection) {
    Result result = Result.Nothing;
    for (Expression<?, ?> st: expressions()) {
      Result r = st.execute(database, connection);
      if (r != Result.Nothing) {
        result = r;
      }
    }
    return result;
  }

  public List<Expression<?, ?>> expressions() {
    return children();
  }
}
