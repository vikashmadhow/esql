/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.cast;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

/**
 * Attempts to cast an expression to a given type, returning null on failure.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class TryCast extends Expression<String, String> {
  public TryCast(Context context, Expression<?, ?> expr, Type toType) {
    super(context, "TryCast",
          T2.of("expr", expr),
          T2.of("type", new Esql<>(context, toType)));
  }

  public TryCast(TryCast other) {
    super(other);
  }

  @SafeVarargs
  public TryCast(TryCast other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public TryCast copy() {
    return new TryCast(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public TryCast copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new TryCast(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return toType();
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    String exprTrans = expr().translate(target, esqlCon, path.add(expr()), parameters, env);
    String typeTrans = toType().translate(target, esqlCon, path, parameters, env);
    return switch (target) {
      case ESQL       -> "trycast(" + exprTrans + " as " + typeTrans + ")";
      case POSTGRESQL -> "_core._try_cast(" + exprTrans + "::text, null::" + typeTrans + ')';
      case JAVASCRIPT -> exprTrans;                                 // ignore cast for Javascript
      default         -> "try_cast(" + exprTrans + " as " + typeTrans + ')';
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr()._toString(st, level, indent);
    st.append("?:").append(toType().name());
  }

  public Type toType() {
    return childValue("type");
  }

  public Expression<?, String> expr() {
    return child("expr");
  }
}
