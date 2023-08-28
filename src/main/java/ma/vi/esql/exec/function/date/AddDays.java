/*
 * Copyright (c) 2018 Vikash Madhow
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
 * Add days to date.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AddDays extends Function {
  public AddDays() {
    super("adddays", Types.IntType,
          Arrays.asList(new FunctionParam("s1", Types.DatetimeType),
                        new FunctionParam("s2", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    String arg1 = args.get(0).translate(target, esqlCon, path.add(args.get(0)), env).toString();
    String arg2 = args.get(1).translate(target, esqlCon, path.add(args.get(1)), env).toString();
    if (target == POSTGRESQL) {
      return arg1 + "concat(" + arg2 + ", ' day')::interval";

    } else if (target == SQLSERVER) {
      return "dateadd(day, " + arg2 + ", " + arg1 + ')';

    } else if (target == JAVASCRIPT) {
      return "new Date(" + arg1 + ".getFullYear(), "
                         + arg1 + ".getMonth(), "
                         + arg1 + ".getDate() + " + arg2 + ")";

    } else {
      return name + '(' + arg1 + ", " + arg2 + ')';
    }
  }
}