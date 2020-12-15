/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.database.Structure;
import ma.vi.esql.type.Types;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.parser.Translatable.Target.*;

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
public class FormatDateFunction extends Function {
  public FormatDateFunction(Structure structure) {
    super("format", Types.TextType,
          Arrays.asList(new FunctionParameter("date", Types.DatetimeType),
            new FunctionParameter("format", Types.TextType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?>> args = call.arguments();
    Expression<?> date = args.get(0);

    String format = null;
    if (args.size() >= 2) {
      format = args.get(1).value(ESQL).toString();
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

      return "to_char(" + date.translate(target) + ", '"
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

      return "format(" + date.translate(target) + ", '"
          + format + "')";
    } else {
      return "format(" + date.translate(target)
          + (format == null ? "" : ", " + args.get(1).translate(target)) + ')';
    }
  }
}