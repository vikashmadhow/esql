/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.scope;

import ma.vi.esql.syntax.EsqlException;

/**
 * An exception thrown during scoping if a symbol is declared when the same
 * symbol already exists in the current scoping context.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SymbolAlreadyDefinedException extends EsqlException {
  public SymbolAlreadyDefinedException(String message) {
    super(message);
  }

  public SymbolAlreadyDefinedException(String message, Throwable cause) {
    super(message, cause);
  }
}