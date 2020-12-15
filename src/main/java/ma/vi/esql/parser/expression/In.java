/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.query.Select;
import ma.vi.esql.type.Type;
import ma.vi.base.tuple.T2;
import ma.vi.esql.type.Types;

import java.util.List;

import static java.util.stream.Collectors.joining;

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
  public String translate(Target target) {
    return expr().translate(target) + (not() ? " not in (" : " in (") +
        (select() != null
         ? select().translate(target)
         : list().stream()
                 .map(e -> e.translate(target))
                 .collect(joining(", "))
        ) + ')';
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
