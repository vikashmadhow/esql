/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.semantic.type.Type;

/**
 * The definition of a function parameter.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class FunctionParameter {
  public FunctionParameter(String name, Type type) {
    this(name, type, false, null);
  }

  public FunctionParameter(String name, Type type, boolean variadic, String defaultValue) {
    this.name = name;
    this.type = type;
    this.variadic = variadic;
    this.defaultValue = defaultValue;
  }

  public final String name;
  public final Type type;
  public final boolean variadic;
  public final String defaultValue;
}
