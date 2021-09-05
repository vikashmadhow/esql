/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;

import java.util.Map;

/**
 * Represents a join between two table expressions. The join type, which can
 * be null (inner, default), 'left', 'right' or 'full' is accessible through
 * the {@link #joinType()} method.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class JoinTableExpr extends AbstractJoinTableExpr {
  public JoinTableExpr(Context context,
                       TableExpr left,
                       String joinType,
                       TableExpr right,
                       Expression<?, String> on) {
    super(context,
          joinType,
          left,
          right,
          T2.of("on", on),
          T2.of("joinType", new Esql<>(context, joinType)));
  }

  public JoinTableExpr(JoinTableExpr other) {
    super(other);
  }

  @Override
  public JoinTableExpr copy() {
    return new JoinTableExpr(this);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return left().translate(target, path.add(left()), parameters)
         + (joinType() == null ? " join " : ' ' + joinType() + " join ")
         + right().translate(target, path.add(right()), parameters) + " on "
         + on().translate(target, path.add(on()), parameters);
  }

  public String joinType() {
    return childValue("joinType");
  }

  public Expression<?, String> on() {
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
