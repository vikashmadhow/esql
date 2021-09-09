/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function.date;

import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParameter;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.syntax.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.syntax.Translatable.Target.SQLSERVER;

/**
 * Function to add two intervals. An interval is duration
 * in the form of a string consisting of comma-separated set of individual period.
 * E.g. '-2y,5d,-10mon,1m,1w,1h,1s,1ms' is a duration of -2 years, 5 days,
 * -10 months, 1 minute, 1 hour, 1 second and 1 millisecond. Note that the suffix
 * 'm' is for minutes while 'mon' is for months.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AddIntervals extends Function {
  public AddIntervals() {
    super("addintervals", Types.StringType,
          Arrays.asList(new FunctionParameter("interval1", Types.StringType),
            new FunctionParameter("interval2", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target, EsqlPath path) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "interval " + args.get(0).translate(target, path.add(args.get(0))) +
          " + interval " + args.get(1).translate(target, path.add(args.get(1)));

    } else if (target == SQLSERVER) {
      return "_core.add_intervals("
          + args.get(0).translate(target, path.add(args.get(0))) + ", "
          + args.get(1).translate(target, path.add(args.get(1))) + ')';

    } else {
      return name + '('
          + args.get(0).translate(target, path.add(args.get(0))) + ", "
          + args.get(1).translate(target, path.add(args.get(1))) + ')';
    }
  }
}