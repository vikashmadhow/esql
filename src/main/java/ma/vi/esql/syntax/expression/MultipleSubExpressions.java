/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.List;

/**
 * Abstract parent of ESQL expressions taking multiple arguments.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class MultipleSubExpressions<V> extends Expression<V, String> {
  public MultipleSubExpressions(Context context, V value, List<Expression<?, ?>> expressions) {
    super(context, value, expressions);
  }

  public MultipleSubExpressions(MultipleSubExpressions<V> other) {
    super(other);
  }

  public MultipleSubExpressions(MultipleSubExpressions<V> other, V value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract MultipleSubExpressions<V> copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract MultipleSubExpressions<V> copy(V value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  public Type type(EsqlPath path) {
    for (Expression<?, ?> e: expressions()) {
      Type type = e.type(path);
      if (!type.equals(Types.NullType)) {
        return type;
      }
    }
    return Types.TopType;
  }

  public List<Expression<?, String>> expressions() {
    return children();
  }
}