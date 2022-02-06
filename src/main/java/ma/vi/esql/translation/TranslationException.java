/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.translation;

import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlException;

/**
 * An exception thrown if any error is detected during parsing or translation
 * of an ESQL statement or part thereof.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class TranslationException extends EsqlException {
  public TranslationException(String message) {
    super(message);
    this.esql = null;
  }

  public TranslationException(Esql<?, ?> esql, String message) {
    super(message + (esql.line != 0
                     ? " (on line " + esql.line + ")"
                     : ""));
    this.esql = esql;
  }

  public TranslationException(Esql<?, ?> esql, String message, Throwable cause) {
    super(message + (esql.line != 0
                     ? " (on line " + esql.line + ")"
                     : ""), cause);
    this.esql = esql;
  }

  public final Esql<?, ?> esql;
}