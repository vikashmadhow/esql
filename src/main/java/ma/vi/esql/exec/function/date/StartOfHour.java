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
import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Function returning the start of the hour of the supplied date.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class StartOfHour extends Function {
  public StartOfHour() {
    super("startofhour", Types.DateType,
          singletonList(new FunctionParam("s", Types.DateType)));
  }

  @Override
  public String translate(FunctionCall         call,
                          Target               target,
                          EsqlConnection       esqlCon,
                          EsqlPath             path,
                          PMap<String, Object> parameters,
                          Environment          env) {
    List<Expression<?, ?>> args = call.arguments();
    String arg = args.get(0).translate(target, esqlCon, path.add(args.get(0)), env).toString();
    if (target == POSTGRESQL) {
      return "date_trunc('hour', " + arg + ")";

    } else if (target == SQLSERVER) {
      // return "datetrunc(hour, " + arg + ")";
      return "datetime2fromparts(year("  + arg + "), "
                              + "month(" + arg + "), "
                              + "day("   + arg + "), "
                              + "datepart(hour, "  + arg + "), "
                              + "0, 0, 0, 0)";

    } else if (target == JAVASCRIPT) {
      return "new Date(" + arg + ".setHours(" + arg + ".getHours(), 0, 0, 0))";

    } else {
      return name + '(' + arg + ')';
    }
  }
}