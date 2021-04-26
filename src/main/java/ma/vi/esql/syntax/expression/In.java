/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * The in operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class In extends Expression<Expression<?, ?>, String> {
  public In(Context context,
            Expression<?, ?> expr,
            boolean not,
            List<Expression<?, ?>> expressionList) {
    super(context, expr,
        T2.of("not", new Esql<>(context, not)),
        T2.of("list", new Esql<>(context, null, expressionList)));
  }

  public In(In other) {
    super(other);
  }

  @Override
  public In copy() {
    if (!copying()) {
      try {
        copying(true);
        return new In(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    return expr().translate(target, parameters) + (not() ? " not in (" : " in (")
        + list().stream()
                .map(e -> e.translate(target, parameters))
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
    return value;
  }

  public Boolean not() {
    return childValue("not");
  }

  public List<Expression<?, String>> list() {
    return child("list").childrenList();
  }
}
