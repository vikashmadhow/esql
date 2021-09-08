/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Interval;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.DefaultValue;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * A interval literal in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class IntervalLiteral extends BaseLiteral<String> {
  public IntervalLiteral(Context context, String value) {
    super(context, value);
  }

  public IntervalLiteral(IntervalLiteral other) {
    super(other);
  }

  public IntervalLiteral(IntervalLiteral other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public IntervalLiteral copy() {
    return new IntervalLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public IntervalLiteral copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new IntervalLiteral(this, value, children);
  }

  @Override
  public Type type(EsqlPath path) {
    return Types.IntervalType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    switch (target) {
      case POSTGRESQL:
        return '\'' + value + "'::interval";

      case SQLSERVER:
        return '\'' + value + '\'';

      case JSON:
      case JAVASCRIPT:
        String e = "new Interval('" + value + "')";
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      case ESQL:
      default:
        return "i'" + value + "'";
    }
  }

  @Override
  public Object value(Translatable.Target target) {
    return target == JSON ? translate(target) : new Interval(value);
  }
}