/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.expression.Expression;

import java.util.Map;

/**
 * Represents a join between two table expressions. The join type, which can
 * be null (inner, default), 'left', 'right' or 'full' is accessible through
 * the {@link #joinType()} method.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class JoinTableExpr extends AbstractJoinTableExpr {
  public JoinTableExpr(Context context, TableExpr left, String joinType, TableExpr right, Expression<?> on) {
    super(context, joinType, left, right);
    child("on", on);
    child("joinType", new Esql<>(context, joinType));
  }

  public JoinTableExpr(JoinTableExpr other) {
    super(other);
  }

  @Override
  public JoinTableExpr copy() {
    if (!copying()) {
      try {
        copying(true);
        return new JoinTableExpr(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    return left().translate(target, parameters)
         + (joinType() == null ? " join " : ' ' + joinType() + " join ")
         + right().translate(target, parameters) + " on "
         + on().translate(target, parameters);
  }

  public String joinType() {
    return childValue("joinType");
  }

  public Expression<?> on() {
    return child("on");
  }

  @Override
  public String toString() {
    return left()
         + (joinType() == null ? "": ' ' + joinType())
         + " join " + right() + " on " + on();
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    left()._toString(st, level, indent);
    st.append(joinType() == null ? "" : " " + joinType())
      .append(" join ");
    right()._toString(st, level, indent);
    st.append(" on ");
    on()._toString(st, level, indent);
  }
}
