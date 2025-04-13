/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.composable.Composable;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

/**
 * Represents a join between two table expressions. The join type, which can
 * be null (inner, default), 'left', 'right' or 'full' is accessible through
 * the {@link #joinType()} method.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class JoinTableExpr extends AbstractJoinTableExpr {
  public JoinTableExpr(Context    context,
                       String     joinType,
                       boolean    lateral,
                       TableExpr  left,
                       TableExpr  right,
                       Expression<?, String> on) {
    super(context,
          joinType,
          left,
          right,
          T2.of("on", on),
          T2.of("lateral", new Esql<>(context, lateral)));
  }

  public JoinTableExpr(JoinTableExpr other) {
    super(other);
  }

  @SafeVarargs
  public JoinTableExpr(JoinTableExpr other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public JoinTableExpr copy() {
    return new JoinTableExpr(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public JoinTableExpr copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new JoinTableExpr(this, value, children);
  }

  /**
   * For left and right outer-joins, only consider the side of the join which
   * is more restrictive (left for left joins and right for right joins). For
   * full outer joins, we consider both sides as the side producing the least
   * number of records is the more restrictive, and we cannot predict which side
   * is going to be the more restrictive one at this point.
   */
  @Override
  public ShortestPath findShortestPath(Composable composable) {
    String joinType = joinType();
    return joinType == null
        || joinType.equals("full") ? super.findShortestPath(composable)
         : joinType.equals("left") ? left().findShortestPath(composable)
         : right().findShortestPath(composable);
  }

  @Override
  public AppliedShortestPath applyShortestPath(ShortestPath shortest, TableExpr root) {
    String joinType = joinType();
    if (joinType == null
     || joinType.equals("full")) {
      return super.applyShortestPath(shortest, root);

    } else if (joinType.equals("left")) {
      AppliedShortestPath applied = left().applyShortestPath(shortest, root);
      return applied == null ? null
           : new AppliedShortestPath(applied.path(),
                                     join(applied.result(), right()),
                                     applied.targetAlias());
    } else {
      AppliedShortestPath applied = right().applyShortestPath(shortest, root);
      return applied == null ? null
           : new AppliedShortestPath(applied.path(),
                                     join(left(), applied.result()),
                                     applied.targetAlias());
    }
  }

  @Override
  protected AbstractJoinTableExpr join(TableExpr left, TableExpr right) {
    return new JoinTableExpr(context, joinType(), lateral(), left, right, on());
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    String leftTrans  = left() .translate(target, esqlCon, path.add(left()),  parameters, env);
    String rightTrans = right().translate(target, esqlCon, path.add(right()), parameters, env);
    if (lateral()) {
      if (target == Target.SQLSERVER) {
        return joinType() == null
             ? leftTrans + " cross apply " + rightTrans
             : leftTrans + " outer apply " + rightTrans;

      } else {
        return leftTrans
             + (joinType() == null ? " join " : ' ' + joinType() + " join ")
             + "lateral "
             + rightTrans + " on "
             + on().translate(target, esqlCon, path.add(on()), parameters, env);
      }
    } else {
      return leftTrans
           + (joinType() == null ? " join " : ' ' + joinType() + " join ")
           + rightTrans + " on "
           + on().translate(target, esqlCon, path.add(on()), parameters, env);
    }
  }

  public Expression<?, String> on() {
    return child("on");
  }

  public boolean lateral() {
    return childValue("lateral");
  }

  @Override
  public String toString() {
    return left()
         + (joinType() == null ? "" : ' ' + joinType())
         + " join " + (lateral() ? "lateral " : "")
         + right() + " on " + on();
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    left()._toString(st, level, indent);
    st.append(joinType() == null ? "" : " " + joinType())
      .append(" join ").append((lateral() ? "lateral " : ""));
    right()._toString(st, level, indent);
    st.append(" on ");
    on()._toString(st, level, indent);
  }
}
