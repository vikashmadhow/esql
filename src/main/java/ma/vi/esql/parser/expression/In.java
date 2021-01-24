/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.query.Select;
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
public class In extends Expression<Expression<?>> {
  public In(Context context, Expression expr, boolean not, List<Expression<?>> expressionList, Select select) {
    super(context, expr,
        T2.of("not", new Esql<>(context, not)),
        T2.of("list", new Esql<>(context, null, expressionList)),
        T2.of("select", select));
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
  public String translate(Target target, Map<String, Object> parameters) {
    return expr().translate(target, parameters) + (not() ? " not in (" : " in (")
        + (select() != null ? select().translate(target, parameters)
                            : list().stream()
                                    .map(e -> e.translate(target, parameters))
                                    .collect(joining(", ")))
        + ')';
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr()._toString(st, level, indent);
    st.append(not() ? " not in (" : " in (");

    if (select() != null) {
      select()._toString(st, level, indent);
    } else {
      boolean first = true;
      for (Expression<?> e: list()) {
        if (first) { first = false; }
        else       { st.append(", "); }
        e._toString(st, level, indent);
      }
    }
    st.append(')');
  }

  public Expression<?> expr() {
    return value;
  }

  public Boolean not() {
    return childValue("not");
  }

  public List<Expression<?>> list() {
    return child("list").childrenList();
  }

  public Select select() {
    return child("select");
  }
}
