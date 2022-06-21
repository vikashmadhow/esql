/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.lang.Integer.parseInt;
import static ma.vi.base.string.Escape.escapeJsonString;

/**
 * A date literal in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class TimeLiteral extends BaseLiteral<String> {
  public TimeLiteral(Context context, String value) {
    super(context, value);
  }

  public TimeLiteral(TimeLiteral other) {
    super(other);
  }

  @SafeVarargs
  public TimeLiteral(TimeLiteral other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public TimeLiteral copy() {
    return new TimeLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public TimeLiteral copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new TimeLiteral(this, value, children);
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
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    return switch(target) {
      case POSTGRESQL -> '\'' + value + "'::"
                       + computeType(path.add(this)).translate(target,
                                                               esqlCon,
                                                               path,
                                                               parameters,
                                                               env);
      case SQLSERVER  -> "cast('" + value + "' as "
                       + computeType(path.add(this)).translate(target,
                                                               esqlCon,
                                                               path,
                                                               parameters,
                                                               env)
                       + ')';
      case JAVASCRIPT -> "new Date('" + value + "')";
      case JSON       -> '"' + escapeJsonString("new Date('" + value + "')") + '"';
      default         -> "d'" + value + "'";
    };
  }

  @Override
  public LocalDateTime exec(Target               target,
                            EsqlConnection       esqlCon,
                            EsqlPath             path,
                            PMap<String, Object> parameters,
                            Environment          env) {
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

  public static LocalDate date(int year, int month, int day) {
    return LocalDate.of(year, month, day);
  }

  public static LocalTime time(int hour, int minute, int second, int millisecond) {
    return LocalTime.of(hour, minute, second, millisecond * 1_000_000);
  }

  public static LocalDateTime date(int year, int month, int day,
                                   int hour, int minute, int second,
                                   int millisecond) {
    return LocalDateTime.of(year, month, day,
                            hour, minute, second,
                            millisecond * 1_000_000);
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