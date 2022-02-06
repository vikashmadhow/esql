/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function;

import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.expression.Expression;

/**
 * The definition of a function parameter.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record FunctionParam(String           name,
                            Type             type,
                            boolean          variadic,
                            Expression<?, ?> defaultValue) {
  public FunctionParam(String name, Type type) {
    this(name, type, false, null);
  }
}