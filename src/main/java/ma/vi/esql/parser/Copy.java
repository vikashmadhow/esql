/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser;

/**
 * Copy protocol implemented by objects that can
 * make a copy of itself.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Copy<T> {
  T copy();

  boolean copying();

  void copying(boolean copying);
}
