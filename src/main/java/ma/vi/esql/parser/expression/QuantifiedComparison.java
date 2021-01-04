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

/**
 * The quantified comparison operator in ESQL takes the form of
 * <pre>
 *   expression comparison-operator ('all'|'any') (select)
 * </pre>
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
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

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr()._toString(st, level, indent);
    st.append(' ').append(compareOp()).append(' ').append(quantifier());
    select()._toString(st, level, indent);
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
