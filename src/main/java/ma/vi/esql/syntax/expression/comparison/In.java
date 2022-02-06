/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * The in operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class In extends Expression<String, String> {
  public In(Context context,
            Expression<?, ?> expr,
            boolean not,
            List<Expression<?, ?>> expressionList) {
    super(context, "In",
          T2.of("expr", expr),
          T2.of("not", new Esql<>(context, not)),
          T2.of("list", new Esql<>(context, null, expressionList)));
  }

  public In(In other) {
    super(other);
  }

  @SafeVarargs
  public In(In other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public In copy() {
    return new In(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public In copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new In(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return expr().translate(target, esqlCon, path.add(expr()), parameters, env) + (not() ? " not in (" : " in (")
        + list().stream()
                .map(e -> e.translate(target, esqlCon, path.add(e), parameters, env))
                .collect(joining(", "))
        + ')';
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr()._toString(st, level, indent);
    st.append(not() ? " not in (" : " in (");

    boolean first = true;
    for (Expression<?, String> e: list()) {
      if (first) { first = false; }
      else       { st.append(", "); }
      e._toString(st, level, indent);
    }
    st.append(')');
  }

  public Expression<?, ?> expr() {
    return child("expr");
  }

  public Boolean not() {
    return childValue("not");
  }

  public List<Expression<?, String>> list() {
    return child("list").children();
  }
}
