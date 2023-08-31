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
 * Function to get the end of the year of a date.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EndOfYear extends Function {
  public EndOfYear() {
    super("endofmonth", Types.DateType,
          singletonList(new FunctionParam("s", Types.DateType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    String arg = args.get(0).translate(target, esqlCon, path.add(args.get(0)), env).toString();
    if (target == POSTGRESQL) {
      return "(date_trunc('year', " + arg + ") + interval '1 year - 1 day')::date";

    } else if (target == SQLSERVER) {
      return "datefromparts(year(" + arg + "), 12, 31)";

    } else if (target == JAVASCRIPT) {
      return "new Date(" + arg + ".getFullYear(), 11, 31)";

    } else {
      return name + '(' + arg + ')';
    }
  }
}