/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Integer.parseInt;
import static java.util.Calendar.*;
import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.translation.Translatable.Target.JSON;

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

  @SafeVarargs
  public DateLiteral(DateLiteral other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public DateLiteral copy() {
    return new DateLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public DateLiteral copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new DateLiteral(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
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
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
    switch (target) {
      case POSTGRESQL:
        return '\'' + value + "'::" + computeType(path.add(this)).translate(target, path, parameters);

      case SQLSERVER:
        return "cast('" + value + "' as " + computeType(path.add(this)).translate(target, path, parameters) + ')';

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
  public Date value(Target target, EsqlPath path) {
    int year = 0, month  = 0, day    = 0,
        hour = 0, minute = 0, second = 0, milli = 0;

    int from = 0;
    int to   = value.indexOf('-');
    if (to != -1) {
      year  = parseInt(value.substring(from, to));

      from  = to + 1;
      to    = value.indexOf('-', from);
      month = parseInt(value.substring(from, to));

      from  = to + 1;
      to    = value.indexOf(' ', from);
      day   = to == -1
            ? parseInt(value.substring(from))
            : parseInt(value.substring(from, to));
      from  = to + 1;
    }

    to = value.indexOf(':', from);
    if (to != -1) {
      hour = parseInt(value.substring(from, to));

      from = to + 1;
      to   = value.indexOf(':', from);
      if (to == -1) {
        minute = parseInt(value.substring(from));
      } else {
        minute = parseInt(value.substring(from, to));

        from = to + 1;
        to   = value.indexOf('.', from);
        if (to == -1) {
          second = parseInt(value.substring(from));
        } else {
          second = parseInt(value.substring(from, to));
          milli  = parseInt(value.substring(to + 1));
        }
      }
    }
    return date(year, month, day, hour, minute, second, milli);
  }

  public static Date date(int year, int month, int day) {
    return date(year, month, day, 0, 0, 0, 0);
  }

  public static Date time(int hour, int minute, int second, int millisecond) {
    return date(0, 0, 0, hour, minute, second, millisecond);
  }

  public static Date date(int year, int month,  int day,
                          int hour, int minute, int second,
                          int millisecond) {
    Calendar c = Calendar.getInstance();
    c.set(YEAR,         year);
    c.set(MONTH,        month);
    c.set(DATE,         day);
    c.set(HOUR_OF_DAY,  hour);
    c.set(MINUTE,       minute);
    c.set(SECOND,       second);
    c.set(MILLISECOND,  millisecond);
    return c.getTime();
  }

  public boolean hasDate() {
    return value.indexOf('-') != -1;
  }

  public boolean hasTime() {
    return value.indexOf(':') != -1;
  }

  public static final SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
  public static final SimpleDateFormat TimeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
  public static final SimpleDateFormat DateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
}