/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.type.Types;

import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.function.DatePart.Part.*;
import static ma.vi.esql.syntax.Translatable.Target.*;

/**
 * Function to extract part (day, month, year, etc.) of a date.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DatePart extends Function {
  public DatePart(String name, Part part) {
    super(name, Types.IntType,
          singletonList(new FunctionParameter("s", Types.DatetimeType)));
    this.part = part;
  }

  public enum Part {
    Year        ("year",          "year"),
    Quarter     ("quarter",       "quarter"),
    Semester    ("semester",      "semester"),
    Month       ("month",         "month"),
    DayOfYear   ("doy",           "dayofyear"),
    Day         ("day",           "day"),
    Week        ("week",          "week"),
    DayOfWeek   ("dow",           "weekday"),
    Hour        ("hour",          "hour"),
    Minute      ("minute",        "minute"),
    Second      ("second",        "second"),
    Millisecond ("milliseconds",  "millisecond"),
    Microsecond ("microseconds",  "microsecond");

    public final String postgresqlName;
    public final String mssqlName;

    Part(String postgresqlName, String mssqlName) {
      this.postgresqlName = postgresqlName;
      this.mssqlName = mssqlName;
    }
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?, ?>> args = call.arguments();
    String arg = args.get(0).translate(target).toString();
    if (target == POSTGRESQL) {
      if (part == Semester) {
        return "case date_part('" + Quarter.postgresqlName + "', " + arg + ") "
             + "  when 1 then 1 "
             + "  when 2 then 1 "
             + "  when 3 then 2 "
             + "  when 4 then 2 "
             + "end";
      } else {
        return "date_part('" + part.postgresqlName + "', " + arg + ')';
      }
    } else if (target == SQLSERVER) {
      if (part == Semester) {
        return "case datepart(" + Quarter.mssqlName + ", " + arg + ") "
             + "  when 1 then 1 "
             + "  when 2 then 1 "
             + "  when 3 then 2 "
             + "  when 4 then 2 "
             + "end";
      } else if (part == Millisecond) {
        // SQL Server does not include seconds in milliseconds: include it as it is
        // more sensible and similar to postgresql
        return "datepart(" + Second.mssqlName + ", " + arg + ") * 1000 + "
             + "datepart(" + part.mssqlName + ", " + arg + ')';

      } else if (part == Microsecond) {
        // SQL Server does not include seconds in microseconds: include it as
        // it is more sensible and similar to postgresql
        return "datepart(" + Second.mssqlName + ", " + arg + ") * 1000000 + "
             + "datepart(" + part.mssqlName + ", " + arg + ')';

      } else {
        return "datepart(" + part.mssqlName + ", " + arg + ')';
      }
    } else if (target == JAVASCRIPT) {
      return switch (part) {
        case Year         -> "(" + arg + ").year()";
        case Quarter      -> "(" + arg + ").quarter()";
        case Semester     -> "((" + arg + ").quarter()==1 || (" + arg + ").quarter()==2 ? 1 : 2)";
        case Month        -> "((" + arg + ").month() + 1)";
        case Week         -> "(" + arg + ").week()";
        case DayOfWeek    -> "((" + arg + ").dayOfWeek() + 1)";
        case DayOfYear    -> "(" + arg + ").dayOfYear()";
        case Day          -> "(" + arg + ").date()";
        case Hour         -> "(" + arg + ").hour()";
        case Minute       -> "(" + arg + ").minute()";
        case Second       -> "(" + arg + ").second()";
        case Millisecond  -> "(" + arg + ").millisecond()";
        case Microsecond  -> "((" + arg + ").millisecond() * 1000)";
      };
    } else {
      return name + '(' + arg + ')';
    }
  }

  public final Part part;
}