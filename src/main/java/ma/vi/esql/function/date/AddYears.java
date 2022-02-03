/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.function.date;

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
 * Add years to date.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AddYears extends Function {
  public AddYears() {
    super("addyears", Types.IntType,
          Arrays.asList(new FunctionParam("s1", Types.DatetimeType),
                        new FunctionParam("s2", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlPath path) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return args.get(0).translate(target)
           + "concat(" + args.get(1).translate(target) + ", ' year')::interval";
    } else if (target == SQLSERVER) {
      return "dateadd(year, "
           + args.get(1).translate(target) + ", "
           + args.get(0).translate(target) + ')';
    } else {
      return name + '(' + args.get(0).translate(target) + ')';
    }
  }
}