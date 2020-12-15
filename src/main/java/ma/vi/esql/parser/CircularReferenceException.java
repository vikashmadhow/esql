/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser;

/**
 * An exception thrown if a circular reference is detected in an Esql
 * statement or expression.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CircularReferenceException extends TranslationException {
  public CircularReferenceException(String message) {
    super(message);
  }

  public CircularReferenceException(String message, Throwable cause) {
    super(message, cause);
  }
}