/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

/**
 * Between range comparison operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Between extends Expression<Expression<?>> {
  public Between(Context context, Expression<?> compare, Expression<?> from, Expression<?> to) {
    super(context, compare, T2.of("from", from), T2.of("to", to));
  }

  public Between(Between other) {
    super(other);
  }

  @Override
  public Between copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Between(this);
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
    switch (target) {
      case JSON:
      case JAVASCRIPT:
        String e = '(' +
            compare().translate(target) + " >= " + from().translate(target) + " && " +
            compare().translate(target) + " <= " + to().translate(target) +
            ')';
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return compare().translate(target) + " between " +
            from().translate(target) + " and " +
            to().translate(target);
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    compare()._toString(st, level, indent);
    st.append(" between ");
    from()._toString(st, level, indent);
    st.append(" and ");
    to()._toString(st, level, indent);
  }

  public Expression<?> compare() {
    return value;
  }

  public Expression<?> from() {
    return child("from");
  }

  public Expression<?> to() {
    return child("to");
  }
}
