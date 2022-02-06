/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

import static ma.vi.esql.translation.Translatable.Target.ESQL;

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

  @SafeVarargs
  public CrossProductTableExpr(CrossProductTableExpr other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public CrossProductTableExpr copy() {
    return new CrossProductTableExpr(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public CrossProductTableExpr copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new CrossProductTableExpr(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return left().translate(target, esqlCon, path.add(left()), parameters, env)
         + (target == ESQL ? " times " : " cross join ")
         + right().translate(target, esqlCon, path.add(right()), parameters, env);
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
