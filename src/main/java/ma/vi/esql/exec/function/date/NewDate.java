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
 * Function to create a new datetime value from individual components.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NewDate extends Function {
  public NewDate() {
    super("newdate", Types.DatetimeType,
         Arrays.asList(new FunctionParam("y", Types.IntType),
                       new FunctionParam("m", Types.IntType),
                       new FunctionParam("d", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "make_date("
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
          + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", "
          + args.get(2).translate(target, esqlCon, path.add(args.get(2)), env) + ')';

    } else if (target == SQLSERVER) {
      return "datefromparts("
        + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
        + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", "
        + args.get(2).translate(target, esqlCon, path.add(args.get(2)), env) + ')';

    } else if (target == JAVASCRIPT) {
      return "new Date("
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
          + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + " - 1, "
          + args.get(2).translate(target, esqlCon, path.add(args.get(2)), env) + ')';

    } else {
      return name + '('
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
          + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", "
          + args.get(2).translate(target, esqlCon, path.add(args.get(2)), env) + ')';
    }
  }
}