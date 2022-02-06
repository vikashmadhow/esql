/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function.date;

import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.function.FunctionCall;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Function to calculate difference between two dates.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DateDiff extends Function {
  public DateDiff(String name, DatePart.Part part) {
    super(name, Types.IntType,
          Arrays.asList(new FunctionParam("s1", Types.DatetimeType),
            new FunctionParam("s2", Types.DatetimeType)));
    this.part = part;
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "extract(" + part.postgresqlName + " from age("
           + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
           + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + "))";
    } else if (target == SQLSERVER) {
      return "datediff(" + part.mssqlName + ", "
           + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", "
           + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ")";
    } else {
      return name + '(' + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ')';
    }
  }

  public final DatePart.Part part;
}