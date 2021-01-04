/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.List;

/**
 * Abstract parent of ESQL expressions taking multiple arguments.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
abstract class MultipleSubExpressions<V> extends Expression<V> {
  public MultipleSubExpressions(Context context, V value, List<Expression<?>> expressions) {
    super(context, value, expressions);
  }

  public MultipleSubExpressions(MultipleSubExpressions<V> other) {
    super(other);
  }

  @Override
  public abstract MultipleSubExpressions<V> copy();

  @Override
  public Type type() {
    for (Expression<?> e: expressions()) {
      Type type = e.type();
      if (!type.equals(Types.NullType)) {
        return type;
      }
    }
    return Types.TopType;
  }

  public List<Expression<?>> expressions() {
    return childrenList();
  }
}