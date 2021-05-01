/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.expression.Expression;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * Between range comparison operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Between extends Expression<Expression<?, ?>, String> {
  public Between(Context context,
                 boolean not,
                 Expression<?, ?> compare,
                 Expression<?, ?> from,
                 Expression<?, ?> to) {
    super(context, compare,
          T2.of("not", new Esql<>(context, not)),
          T2.of("from", from),
          T2.of("to", to));
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
  protected String trans(Target target, Map<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT:
        String e = (not() ? "!" : "") + '('
                 + compare().translate(target, parameters) + " >= " + from().translate(target, parameters) + " && "
                 + compare().translate(target, parameters) + " <= " + to().translate(target, parameters) + ')';
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return compare().translate(target, parameters)
             + (not() ? " not" : "") + " between "
             + from().translate(target, parameters) + " and "
             + to().translate(target, parameters);
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    compare()._toString(st, level, indent);
    if (not()) {
      st.append(" not");
    }
    st.append(" between ");
    from()._toString(st, level, indent);
    st.append(" and ");
    to()._toString(st, level, indent);
  }

  public boolean not() {
    return childValue("not");
  }

  public Expression<?, ?> compare() {
    return value;
  }

  public Expression<?, ?> from() {
    return child("from");
  }

  public Expression<?, ?> to() {
    return child("to");
  }
}
