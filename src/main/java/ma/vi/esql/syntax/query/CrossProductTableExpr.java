/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.esql.syntax.Context;

import java.util.Map;

import static ma.vi.esql.syntax.Translatable.Target.ESQL;

/**
 * Represents a cross-product (cartesian product) over two table expressions.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CrossProductTableExpr extends AbstractJoinTableExpr {
  public CrossProductTableExpr(Context context, TableExpr left, TableExpr right) {
    super(context, "cross", left, right);
  }

  public CrossProductTableExpr(CrossProductTableExpr other) {
    super(other);
  }

  @Override
  public CrossProductTableExpr copy() {
    if (!copying()) {
      try {
        copying(true);
        return new CrossProductTableExpr(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    return left().translate(target, parameters)
        + (target == ESQL ? " times " : " cross join ")
        + right().translate(target, parameters);
  }

  @Override
  public String toString() {
    return left() + " * " + right();
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    left()._toString(st, level, indent);
    st.append(" * ");
    right()._toString(st, level, indent);
  }
}
