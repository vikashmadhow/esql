/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.type;

/**
 * A exception thrown when a reference is made to a column with
 * the same name existing in multiple sub-objects (such as multiple
 * relations in a join).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AmbiguousColumnException extends RuntimeException {
  public AmbiguousColumnException(Throwable cause) {
    super(cause);
  }

  public AmbiguousColumnException(String message, Throwable cause) {
    super(message, cause);
  }

  public AmbiguousColumnException(String message) {
    super(message);
  }

  public AmbiguousColumnException() {
  }
}