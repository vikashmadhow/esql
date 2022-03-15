/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.date;

import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Formats a date to a text, supporting various formats:
 * <p>
 * format(date) formats date as day-month-year with month in 3-letters
 * format(date, pattern) format according to pattern specification where
 * pattern can contain the following:
 * <p>
 * . Day: full day name
 * . Dy:  abbreviated day name
 * . Dd:  Day number (00-31)
 * <p>
 * . Month: Full month name
 * . Mon: abbreviated month name
 * . Mo: month number
 * <p>
 * . Year: 4 digits year
 * . Yr: 2 digits of year
 * <p>
 * . Hh: Hour of day (0-23)
 * . Hr: Hour of day (01-12)
 * <p>
 * . Mi: Minutes (00-59)
 * . Se: Seconds (00-59)
 * <p>
 * . Ms: Milliseconds
 * <p>
 * . Am or Pm: meridian indicator
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class FormatDate extends Function {
  public FormatDate() {
    super("format", Types.StringType,
          Arrays.asList(new FunctionParam("date", Types.DatetimeType),
            new FunctionParam("format", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    Expression<?, ?> date = args.get(0);

    String format = null;
    if (args.size() >= 2) {
      format = args.get(1).exec(target, esqlCon, path, parameters, env).toString();
    }
    if (target == POSTGRESQL) {
      if (format == null) {
        format = "DD-Mon-YYYY";
      } else {
        format = format.toLowerCase()
                       .replace("day", "Day")
                       .replace("dy", "Dy")
                       .replace("dd", "DD")

                       .replace("month", "Month")
                       .replace("mon", "Mon")
                       .replace("mo", "MM")

                       .replace("year", "YYYY")
                       .replace("yr", "YY")

                       .replace("hh", "HH24")
                       .replace("hr", "HH12")

                       .replace("mi", "MI")

                       .replace("se", "SS")

                       .replace("ms", "MS");
      }

      return "to_char(" + date.translate(target, esqlCon, path.add(date), env) + ", '"
          + format + "')";

    } else if (target == SQLSERVER) {
      if (format == null) {
        format = "dd-MMM-yyyy";
      } else {
        format = format.toLowerCase()
                       .replace("day", "dddd")
                       .replace("dy", "ddd")

                       .replace("month", "MMMM")
                       .replace("mon", "MMM")
                       .replace("mo", "MM")

                       .replace("year", "yyyy")
                       .replace("yr", "yy")

                       .replace("hh", "HH")
                       .replace("hr", "hh")

                       .replace("mi", "mm")

                       .replace("se", "ss")

                       .replace("ms", "fff")

                       .replace("am", "tt")
                       .replace("pm", "tt");
      }

      return "format(" + date.translate(target, esqlCon, path.add(date), env) + ", '" + format + "')";
    } else {
      return "format(" + date.translate(target, esqlCon, path.add(date), env)
          + (format == null ? "" : ", " + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env)) + ')';
    }
  }
}