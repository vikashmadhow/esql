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

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

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
          Arrays.asList(new FunctionParam("interval1", Types.StringType),
            new FunctionParam("interval2", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "interval " + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) +
          " + interval " + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env);

    } else if (target == SQLSERVER) {
      return "_core.add_intervals("
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
          + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ')';

    } else {
      return name + '('
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
          + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ')';
    }
  }
}