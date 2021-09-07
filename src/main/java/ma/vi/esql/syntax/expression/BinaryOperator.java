/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * Parent of binary operators in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class BinaryOperator extends DoubleSubExpressions<String> {
  public BinaryOperator(Context context,
                        String op,
                        Expression<?, ?> expr1,
                        Expression<?, ?> expr2) {
    super(context, op, expr1, expr2);
  }

  public BinaryOperator(BinaryOperator other) {
    super(other);
  }

  public BinaryOperator(BinaryOperator other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract BinaryOperator copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract BinaryOperator copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  protected String trans(Target target,
                         EsqlPath path,
                         Map<String, Object> parameters) {
    String e = expr1().translate(target, path.add(expr1()), parameters)
             + ' ' + op() + ' '
             + expr2().translate(target, path.add(expr2()), parameters);
    return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
  }

  public String op() {
    return value;
  }
}
