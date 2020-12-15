/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser;

/**
 * An exception thrown if any error is detected during parsing or translation
 * of an ESQL statement or part thereof.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class TranslationException extends EsqlException {
  public TranslationException(String message) {
    super(message);
  }

  public TranslationException(String message, Throwable cause) {
    super(message, cause);
  }
}