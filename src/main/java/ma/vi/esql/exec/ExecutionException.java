/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlException;

/**
 * An exception thrown during execution of an ESQL program.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ExecutionException extends EsqlException {
  public ExecutionException(Esql<?, ?> esql, String message) {
    super(message + (esql.line != 0
                  ? " (on line " + esql.line + ")"
                  : ""));
    this.esql = esql;
  }

  public ExecutionException(Esql<?, ?> esql, String message, Throwable cause) {
    super(message + (esql.line != 0
                  ? " (on line " + esql.line + ")"
                  : ""), cause);
    this.esql = esql;
  }

  public final Esql<?, ?> esql;
}