/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

import java.util.List;

/**
 * Base literals are literals of simple types.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class BaseLiteral<V> extends Literal<V> {
  public BaseLiteral(Context context, V value) {
    super(context, value);
  }

  @SafeVarargs
  public BaseLiteral(Context context,
                     V value,
                     T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public BaseLiteral(Context context,
                     V value,
                     List<? extends Esql<?, ?>> children) {
    super(context, value, children);
  }

  public BaseLiteral(BaseLiteral<V> other) {
    super(other);
  }

  @SafeVarargs
  public BaseLiteral(BaseLiteral<V> other, V value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract BaseLiteral<V> copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract BaseLiteral<V> copy(V value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(value);
  }

  @Override
  public Object exec(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return value;
  }
}