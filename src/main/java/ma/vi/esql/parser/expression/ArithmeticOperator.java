/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.BaseType;
import ma.vi.esql.type.Type;

/**
 * An arithmetic operator works on numbers and implement the necessary
 * type promotion (int -> long -> float -> double, etc.).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
abstract class ArithmeticOperator extends BinaryOperator {
  public ArithmeticOperator(Context context, String op, Expression<?> expr1, Expression<?> expr2) {
    super(context, op, expr1, expr2);
  }

  public ArithmeticOperator(ArithmeticOperator other) {
    super(other);
  }

  @Override
  public abstract ArithmeticOperator copy();

  @Override
  public Type type() {
    Type leftType = expr1().type();
    Type rightType = expr2().type();

    if (leftType instanceof BaseType && rightType instanceof BaseType) {
      BaseType left = (BaseType)leftType;
      BaseType right = (BaseType)rightType;

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
