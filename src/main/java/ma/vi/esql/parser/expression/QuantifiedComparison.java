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

public class QuantifiedComparison extends Expression<Expression<?>> {
  public QuantifiedComparison(Context context, Expression<?> expr, String compareOp,
                              String quantifier, Select select) {
    super(context, expr,
        T2.of("compareOp", new Esql<>(context, compareOp)),
        T2.of("quantifier", new Esql<>(context, quantifier)),
        T2.of("select", select));
  }

  public QuantifiedComparison(QuantifiedComparison other) {
    super(other);
  }

  @Override
  public QuantifiedComparison copy() {
    if (!copying()) {
      try {
        copying(true);
        return new QuantifiedComparison(this);
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
    return expr().translate(target) + ' ' + compareOp() + ' ' + quantifier() +
        select().translate(target);
  }

  public Expression<?> expr() {
    return value;
  }

  public String compareOp() {
    return childValue("compareOp");
  }

  public String quantifier() {
    return childValue("quantifier");
  }

  public Select select() {
    return child("select");
  }
}
