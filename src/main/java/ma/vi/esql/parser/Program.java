/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser;

import ma.vi.esql.database.Database;
import ma.vi.esql.exec.Result;

import java.sql.Connection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * A sequence of ESQL statements is a program.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Program extends Esql<String, List<?>> {
  public Program(Context context, List<Statement<?, ?>> statements) {
    super(context, "program", statements);
  }

  public Program(Program other) {
    super(other);
  }

  @Override
  public Program copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Program(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public List<?> translate(Target target) {
    return statements().stream()
                       .map(s -> s.translate(target))
                       .collect(toList());
  }

  @Override
  public Result execute(Database database, Connection connection) {
    Result result = Result.Nothing;
    for (Statement<?, ?> st: statements()) {
      Result r = st.execute(database, connection);
      if (r != Result.Nothing) {
        result = r;
      }
    }
    return result;
  }

  public List<Statement<?, ?>> statements() {
    return childrenList();
  }
}