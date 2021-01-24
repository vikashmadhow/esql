/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.base.lang.Errors;
import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.parser.Translatable.Target.JSON;

/**
 * A date literal in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DateLiteral extends BaseLiteral<String> {
  public DateLiteral(Context context, String value) {
    super(context, value);
  }

  public DateLiteral(DateLiteral other) {
    super(other);
  }

  @Override
  public DateLiteral copy() {
    if (!copying()) {
      try {
        copying(true);
        return new DateLiteral(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    boolean containsDate = hasDate();
    boolean containsTime = hasTime();

    if (containsDate && !containsTime) {
      return Types.DateType;

    } else if (!containsDate && containsTime) {
      return Types.TimeType;

    } else {
      return Types.DatetimeType;
    }
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    Type type = type();
    switch (target) {
      case POSTGRESQL:
        return '\'' + value + "'::" + type().translate(target, parameters);

      case SQLSERVER:
        return "cast('" + value + "' as " + type().translate(target, parameters) + ')';

      case JSON:
      case JAVASCRIPT:
        String e = "new Date('" + value + "')";
        return target == JSON ? '"' + escapeJsonString(e) + '"' : e;

      case ESQL:
      default:
        return "d'" + value + "'";
    }
  }

  @Override
  public Date value(Target target) {
    try {
      Type type = type();
      if (type == Types.DateType) {
        return DateFormat.parse(value);

      } else if (type == Types.TimeType) {
        return TimeFormat.parse(value);

      } else {
        return DateTimeFormat.parse(value);
      }
    } catch (ParseException pe) {
      throw Errors.unchecked(pe);
    }
  }

  public boolean hasDate() {
    return value.indexOf('-') != -1;
  }

  public boolean hasTime() {
    return value.indexOf(':') != -1;
  }

  public static final SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
  public static final SimpleDateFormat TimeFormat = new SimpleDateFormat("HH:mm:ss");
  public static final SimpleDateFormat DateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}