/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

/**
 * An exception in the data form, integrity, etc.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DataException extends RuntimeException {
  public DataException(String message) {
    super(message);
  }

  public DataException(String message, Throwable cause) {
    super(message, cause);
  }
}