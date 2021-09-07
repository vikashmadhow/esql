/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.BaseType;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.expression.BinaryOperator;
import ma.vi.esql.syntax.expression.DefaultValue;
import ma.vi.esql.syntax.expression.Expression;

/**
 * An arithmetic operator works on numbers and implement the necessary
 * type promotion (int -> long -> float -> double, etc.).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
abstract class ArithmeticOperator extends BinaryOperator {
  public ArithmeticOperator(Context context,
                            String op,
                            Expression<?, String> expr1,
                            Expression<?, String> expr2) {
    super(context, op, expr1, expr2);
  }

  public ArithmeticOperator(ArithmeticOperator other) {
    super(other);
  }

  public ArithmeticOperator(ArithmeticOperator other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract ArithmeticOperator copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract ArithmeticOperator copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  public Type type() {
    Type leftType = expr1().type();
    Type rightType = expr2().type();

    if (leftType instanceof BaseType left
     && rightType instanceof BaseType right) {

      // Non-integral trumps integral
      if (left.integral && !right.integral) {
        return right;

      } else if (!left.integral && right.integral) {
        return left;

      } else {
        // return larger-sized type
        return left.size > right.size ? left : right;
      }
    } else if (leftType instanceof BaseType) {
      return leftType;

    } else {
      return rightType;
    }
  }
}
