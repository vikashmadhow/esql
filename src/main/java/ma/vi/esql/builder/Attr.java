/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.builder;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Attr {
  public Attr(String name, String expr) {
    this.name = name;
    this.expr = expr;
  }

  public static Attr of(String name, String expr) {
    return new Attr(name, expr);
  }

  public final String name;
  public final String expr;
}
