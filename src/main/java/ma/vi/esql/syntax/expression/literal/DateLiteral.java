/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.lang.Errors;
import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

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
  public Type type(EsqlPath path) {
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
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    switch (target) {
      case POSTGRESQL:
        return '\'' + value + "'::" + type(path.add(this)).translate(target, path, parameters);

      case SQLSERVER:
        return "cast('" + value + "' as " + type(path.add(this)).translate(target, path, parameters) + ')';

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
    try {
      Type type = type(path.add(this));
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