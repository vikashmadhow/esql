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

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Function to set the year of a date.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SetYear extends Function {
  public SetYear() {
    super("setyear", Types.DateType,
          Arrays.asList(new FunctionParam("d", Types.DateType),
                        new FunctionParam("h", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall         call,
                          Target               target,
                          EsqlConnection       esqlCon,
                          EsqlPath             path,
                          PMap<String, Object> parameters,
                          Environment          env) {
    List<Expression<?, ?>> args = call.arguments();
    String arg1 = args.get(0).translate(target, esqlCon, path.add(args.get(0)), env).toString();
    String arg2 = args.get(1).translate(target, esqlCon, path.add(args.get(1)), env).toString();
    if (target == POSTGRESQL) {
      return "make_timestamp("                            + arg2 +  ", "
                          + "date_part('month', "         + arg1 + "), "
                          + "date_part('day', "           + arg1 + "), "
                          + "date_part('hour', "          + arg1 + "), "
                          + "date_part('minute', "        + arg1 + "), "
                          + "date_part('second', "        + arg1 + "), "
                          + "date_part('milliseconds', "  + arg1 + "), "
                          + "date_part('microseconds', "  + arg1 + "))";

    } else if (target == SQLSERVER) {
      return "datetime2fromparts("                        + arg2 +  ", "
                              + "datepart(month, "        + arg1 + "), "
                              + "datepart(day, "          + arg1 + "), "
                              + "datepart(hour, "         + arg1 + "), "
                              + "datepart(minute, "       + arg1 + "), "
                              + "datepart(second, "       + arg1 + "), "
                              + "datepart(millisecond, "  + arg1 + "), "
                              + "datepart(microsecond, "  + arg1 + "))";

    } else if (target == JAVASCRIPT) {
      return "new Date(" + arg1 + ".setFullYear(" + arg2 + "))";

    } else {
      return name + '(' + arg1 + ", " + arg2 + ')';
    }
  }
}