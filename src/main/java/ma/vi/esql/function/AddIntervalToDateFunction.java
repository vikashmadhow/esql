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

import static ma.vi.esql.parser.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.parser.Translatable.Target.SQLSERVER;

/**
 * Function for add an interval to a date / datetime. An interval is duration
 * in the form of a string consisting of comma-separated set of individual period.
 * E.g. '-2y,5d,-10mon,1m,1w,1h,1s,1ms' is a duration of -2 years, 5 days,
 * -10 months, 1 minute, 1 hour, 1 second and 1 millisecond. Note that the suffix
 * 'm' is for minutes while 'mon' is for months.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AddIntervalToDateFunction extends Function {
  public AddIntervalToDateFunction(Structure structure) {
    super("incdate", Types.DatetimeType,
          Arrays.asList(new FunctionParameter("date", Types.DatetimeType),
            new FunctionParameter("interval", Types.IntervalType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return '(' + args.get(0).translate(target) + " + " +
          args.get(1).translate(target) + ')';

    } else if (target == SQLSERVER) {
      return "_core.add_interval("
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ')';

    } else {
      return name + '('
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ')';
    }
  }
}