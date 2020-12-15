/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

/**
 * A parameter whose value is to be parsed as an expression
 * instead of assumed to be a literal of the proper type.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ExpressionParam extends Param {
  public ExpressionParam(String name, Object value) {
    super(name, value);
  }

  public static ExpressionParam of(String name, Object value) {
    return new ExpressionParam(name, value);
  }
}
