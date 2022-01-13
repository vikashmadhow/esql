/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.stream.Stream;

/**
 * Abstract parent of ESQL expressions taking exactly one argument.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class SingleSubExpression extends Expression<String, String> {
  @SafeVarargs
  public SingleSubExpression(Context context,
                             String value,
                             Expression<?, String> expr,
                             T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value,
          Stream.concat(
              Stream.of(T2.of("expr", expr)),
              Stream.of(children)).toArray(T2[]::new));
  }

  public SingleSubExpression(SingleSubExpression other) {
    super(other);
  }

  @SafeVarargs
  public SingleSubExpression(SingleSubExpression other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract SingleSubExpression copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract SingleSubExpression copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  public Type computeType(EsqlPath path) {
    return expr().computeType(path.add(expr()));
  }

  public Expression<?, String> expr() {
    return child("expr");
  }
}