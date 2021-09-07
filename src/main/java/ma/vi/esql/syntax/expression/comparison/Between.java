/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
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

  public Between(Between other, Expression<?, ?> value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public Between copy() {
    return new Between(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public Between copy(Expression<?, ?> value, T2<String, ? extends Esql<?, ?>>... children) {
    return new Between(this, value, children);
  }

  @Override
  public Type type() {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    switch (target) {
      case JSON, JAVASCRIPT:
        Object compareEx = compare().translate(target, path.add(compare()), parameters);
        String e = (not() ? "!" : "") + '('
                 + compareEx + " >= " + from().translate(target, path.add(from()), parameters) + " && "
                 + compareEx + " <= " + to().translate(target, path.add(to()), parameters) + ')';
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      default:
        return compare().translate(target, path.add(compare()), parameters)
             + (not() ? " not" : "") + " between "
             + from().translate(target, path.add(from()), parameters) + " and "
             + to().translate(target, path.add(to()), parameters);
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
