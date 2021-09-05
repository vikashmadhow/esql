/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.arithmetic;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.BaseType;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.expression.BinaryOperator;
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

  @Override
  public abstract ArithmeticOperator copy();

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
