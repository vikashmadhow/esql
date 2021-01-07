/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser;

/**
 * An exception thrown if any error is detected during parsing
 * of an ESQL statement or part thereof.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SyntaxException extends EsqlException {
  public SyntaxException(String message) {
    super(message);
  }

  public SyntaxException(String message, Throwable cause) {
    super(message, cause);
  }
}