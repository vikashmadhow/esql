/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.string;

import ma.vi.esql.database.EsqlConnection;
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

import static ma.vi.esql.semantic.type.Types.*;
import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Formats a date or number to a text, supporting various formats:
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
 * <p>
 * . N0 to N5: number with 0 to 5 decimal places
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Format extends Function {
  public Format() {
    super("format", Types.StringType,
          Arrays.asList(new FunctionParam("value",  Types.Any),
                        new FunctionParam("format", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall         call,
                          Target               target,
                          EsqlConnection       esqlCon,
                          EsqlPath             path,
                          PMap<String, Object> parameters,
                          Environment          env) {
    List<Expression<?, ?>> args  = call.arguments();
    Expression<?, ?> value = args.get(0);

    String valueTrans = value.translate(target, esqlCon, path.add(value), env).toString();
    String format = null;
    if (args.size() >= 2) {
      format = args.get(1).exec(target, esqlCon, path, parameters, env).toString();
    }
    if (target == POSTGRESQL) {
      if (format == null) {
        format = value.type() == DateType     ? "DD-Mon-YYYY"
               : value.type() == TimeType     ? "HH24:MI:SS"
               : value.type() == DatetimeType ? "DD-Mon-YYYY HH24:MI:SS"
               : value.type() == ByteType
              || value.type() == ShortType
              || value.type() == IntType
              || value.type() == LongType     ? "FM9G999G999G999G999G999G999G999"
               : value.type() == FloatType
              || value.type() == DoubleType   ? "FM9G999G999G999G999G999G999G999D99"
               :                                "";
      } else {
        format = format.toLowerCase()
                       .replace("day", "Day")
                       .replace("dy",  "Dy")
                       .replace("dd",  "DD")

                       .replace("month", "Month")
                       .replace("mon",   "Mon")
                       .replace("mo",    "MM")

                       .replace("year", "YYYY")
                       .replace("yr",   "YY")

                       .replace("hh", "HH24")
                       .replace("hr", "HH12")

                       .replace("mi", "MI")
                       .replace("se", "SS")
                       .replace("ms", "MS")

                       .replace("n0", "FM9G999G999G999G999G999G999G999")
                       .replace("n1", "FM9G999G999G999G999G999G999G999D9")
                       .replace("n2", "FM9G999G999G999G999G999G999G999D99")
                       .replace("n3", "FM9G999G999G999G999G999G999G999D999")
                       .replace("n4", "FM9G999G999G999G999G999G999G999D9999")
                       .replace("n5", "FM9G999G999G999G999G999G999G999D99999");
      }
      return value.type() == BoolType   ? "case " + valueTrans + " when true then 'Yes' else 'No' end"
           : value.type() == DateType
          || value.type() == TimeType
          || value.type() == DatetimeType
          || value.type() == ByteType
          || value.type() == ShortType
          || value.type() == IntType
          || value.type() == LongType
          || value.type() == FloatType
          || value.type() == DoubleType ? "to_char(" + valueTrans + ", '" + format + "')"
           :                              "(" + valueTrans + ")::text";

    } else if (target == SQLSERVER) {
      if (format == null) {
        format = value.type() == DateType     ? "dd-MMM-yyyy"
               : value.type() == TimeType     ? "HH:mm:ss"
               : value.type() == DatetimeType ? "dd-MMM-yyyy HH:mm:ss"
               : value.type() == ByteType
              || value.type() == ShortType
              || value.type() == IntType
              || value.type() == LongType     ? "N0"
               : value.type() == FloatType
              || value.type() == DoubleType   ? "N2"
               :                                "";
      } else {
        format = format.toLowerCase()
                       .replace("day", "dddd")
                       .replace("dy",  "ddd")

                       .replace("month", "MMMM")
                       .replace("mon",   "MMM")
                       .replace("mo",    "MM")

                       .replace("year", "yyyy")
                       .replace("yr",   "yy")

                       .replace("hh", "HH")
                       .replace("hr", "hh")

                       .replace("mi", "mm")

                       .replace("se", "ss")

                       .replace("ms", "fff")

                       .replace("am", "tt")
                       .replace("pm", "tt")

                       .toUpperCase();
      }
      return value.type() == BoolType   ? "iif(" + valueTrans + " = 1, 'Yes', 'No')"
           : value.type() == DateType
          || value.type() == TimeType
          || value.type() == DatetimeType
          || value.type() == ByteType
          || value.type() == ShortType
          || value.type() == IntType
          || value.type() == LongType
          || value.type() == FloatType
          || value.type() == DoubleType ? "format(" + valueTrans + ", '" + format + "')"
           :                              "cast("   + valueTrans + " as nvarchar(max))";
    } else {
      return "format(" + valueTrans
           + (format == null ? "" : ", " + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env)) + ')';
    }
  }
}