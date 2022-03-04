/*
 * Copyright (c) 2018 Vikash Madhow
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
import org.pcollections.PMap;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

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
    if (target == POSTGRESQL) {
      return args.get(0).translate(target)
           + "concat(" + args.get(1).translate(target) + ", ' day')::interval";
    } else if (target == SQLSERVER) {
      return "dateadd(day, "
           + args.get(1).translate(target) + ", "
           + args.get(0).translate(target) + ')';
    } else {
      return name + '(' + args.get(0).translate(target) + ')';
    }
  }
}