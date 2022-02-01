/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.esql.syntax.EsqlException;

/**
 * An exception thrown during execution of an ESQL program.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ExecutionException extends EsqlException {
  public ExecutionException(String message) {
    super(message);
  }

  public ExecutionException(String message, Throwable cause) {
    super(message, cause);
  }
}