/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.date;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.exec.function.date.DatePart.Part.*;
import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Function to extract part (day, month, year, etc.) of a date.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DatePart extends Function {
  public DatePart(String name, Part part) {
    super(name, Types.IntType,
          singletonList(new FunctionParam("s", Types.DatetimeType)));
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
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    String arg = args.get(0).translate(target, esqlCon, path.add(args.get(0)), env).toString();
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
             + "datepart(" + part.mssqlName   + ", " + arg + ')';

      } else if (part == Microsecond) {
        // SQL Server does not include seconds in microseconds: include it as
        // it is more sensible and similar to postgresql
        return "datepart(" + Second.mssqlName + ", " + arg + ") * 1000000 + "
             + "datepart(" + part.mssqlName   + ", " + arg + ')';

      } else if (part == DayOfWeek) {
        // Day of week is 1 for Sunday; normalise to Monday being the first day
        // of the week to be consistent with translation to other databases.
        return "iif(datepart(weekday, " + arg + ") = 1, 7, "
                 + "datepart(weekday, " + arg + ") - 1)";

      } else {
        return "datepart(" + part.mssqlName + ", " + arg + ')';
      }
    } else if (target == JAVASCRIPT) {
      return switch (part) {
        case Year         -> "("  + arg + ").getFullYear()";
        case Quarter      -> "(Math.floor(" + arg + ".getMonth() / 3) + 1)";
        case Semester     -> "(Math.floor(" + arg + ".getMonth() / 6) + 1)";
        case Month        -> "((" + arg + ").getMonth() + 1)";
        case Week         -> "("  + arg + ").week()";
        case DayOfWeek    -> "(("  + arg + ").getDay() || 7)";
        case DayOfYear    -> "("  + arg + ").dayOfYear()";
        case Day          -> "("  + arg + ").getDate()";
        case Hour         -> "("  + arg + ").getHours()";
        case Minute       -> "("  + arg + ").getMinutes()";
        case Second       -> "("  + arg + ").getSeconds()";
        case Millisecond  -> "("  + arg + ").getMilliseconds()";
        case Microsecond  -> "((" + arg + ").getMilliseconds() * 1000)";
      };
    } else {
      return name + '(' + arg + ')';
    }
  }

  public final Part part;
}