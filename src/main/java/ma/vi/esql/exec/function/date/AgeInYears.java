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

import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Calculate the age in full years.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AgeInYears extends Function {
  public AgeInYears() {
    super("ageinyears", Types.IntType,
          Arrays.asList(new FunctionParam("s1", Types.DatetimeType),
                        new FunctionParam("s2", Types.DatetimeType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "extract(year from age("
           + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
           + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + "))";
    } else if (target == SQLSERVER) {
      return "case when dateadd(year, datediff(year, "
           + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", "
           + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + "), "
           + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ") > "
           + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env)
           + "  then datediff(hour, " + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", " + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ") / 8766 "
           + "  else datediff(hour, " + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", " + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ") / 8760 "
           + "end";
    } else {
      return name + '(' + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ')';
    }
  }
}