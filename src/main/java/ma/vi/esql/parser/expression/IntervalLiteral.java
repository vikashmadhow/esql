/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Interval;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

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
    if (!copying()) {
      try {
        copying(true);
        return new IntervalLiteral(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.IntervalType;
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
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
  public Object value(Target target) {
    return target == JSON ? translate(target) : new Interval(value);
  }
}