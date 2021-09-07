/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;

/**
 * Abstract parent of ESQL expressions taking exactly two arguments.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class NegatableDoubleSubExpressions<V> extends Expression<V, String> {
  public NegatableDoubleSubExpressions(Context context,
                                       V value,
                                       boolean not,
                                       Expression<?, ?> expr1,
                                       Expression<?, ?> expr2) {
    super(context, value,
          T2.of("not", new Esql<>(context, not)),
          T2.of("expr1", expr1),
          T2.of("expr2", expr2));
  }

  public NegatableDoubleSubExpressions(NegatableDoubleSubExpressions<V> other) {
    super(other);
  }

  public NegatableDoubleSubExpressions(NegatableDoubleSubExpressions<V> other, V value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract NegatableDoubleSubExpressions<V> copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract NegatableDoubleSubExpressions<V> copy(V value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  public Type type() {
    return Types.BoolType;
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr1()._toString(st, level, indent);
    st.append(' ').append(value).append(' ');
    expr2()._toString(st, level, indent);
  }

  public boolean not() {
    return childValue("not");
  }

  public Expression<?, String> expr1() {
    return child("expr1");
  }

  public Expression<?, String> expr2() {
    return child("expr2");
  }
}
