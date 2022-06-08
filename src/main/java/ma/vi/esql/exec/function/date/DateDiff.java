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
  public String translate(FunctionCall         call,
                          Target               target,
                          EsqlConnection       esqlCon,
                          EsqlPath             path,
                          PMap<String, Object> parameters,
                          Environment          env) {
    List<Expression<?, ?>> args = call.arguments();
    return switch(target) {
      case POSTGRESQL -> "extract(" + part.postgresqlName + " from age("
           + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
           + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + "))";

      case SQLSERVER -> "datediff(" + part.mssqlName + ", "
           + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", "
           + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ")";

      default -> name + '(' + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ')';
    };
  }

  public final DatePart.Part part;
}