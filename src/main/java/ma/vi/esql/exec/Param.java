/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.base.tuple.T2;

/**
 * A parameter value passed to a query for execution.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Param extends T2<String, Object> {
  public Param(String name, Object value) {
    super(name, value);
  }

  public static Param of(String name, Object value) {
    return new Param(name, value);
  }
}
