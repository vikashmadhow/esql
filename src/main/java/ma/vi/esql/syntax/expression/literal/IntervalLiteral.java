/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Interval;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;

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

  @Override
  public IntervalLiteral copy() {
    return new IntervalLiteral(this);
  }

  @Override
  public Type type() {
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