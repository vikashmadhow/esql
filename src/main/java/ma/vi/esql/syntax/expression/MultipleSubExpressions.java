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
public abstract class MultipleSubExpressions extends Expression<String, String> {
  public MultipleSubExpressions(Context context,
                                String  value,
                                List<Expression<?, ?>> expressions) {
    super(context, value, expressions);
  }

  public MultipleSubExpressions(MultipleSubExpressions other) {
    super(other);
  }

  @SafeVarargs
  public MultipleSubExpressions(MultipleSubExpressions other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract MultipleSubExpressions copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract MultipleSubExpressions copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  public Type computeType(EsqlPath path) {
    for (Expression<?, ?> e: expressions()) {
      Type type = e.computeType(path.add(e));
      if (!type.equals(Types.NullType)) {
        return type;
      }
    }
    return Types.Any;
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(value).append('(');
    boolean first = true;
    for (Esql<?, ?> child: children()) {
      if (first) first = false;
      else       st.append(", ");
      child._toString(st, level, indent);
    }
    st.append(')');
  }

  public List<Expression<?, ?>> expressions() {
    return children();
  }
}