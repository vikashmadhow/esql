/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.builder;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record Attr(String name, String expr) {
  public static Attr of(String name, String expr) {
    return new Attr(name, expr);
  }
}
