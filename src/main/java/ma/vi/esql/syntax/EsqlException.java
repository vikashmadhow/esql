/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

/**
 * An exception thrown if any error is detected during parsing, translation
 * or execution of an ESQL statement or part thereof.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EsqlException extends RuntimeException {
  public EsqlException(String message) {
    super(message);
  }

  public EsqlException(String message, Throwable cause) {
    super(message, cause);
  }
}