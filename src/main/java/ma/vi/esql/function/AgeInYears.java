/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.database.Structure;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.type.Types;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.parser.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.parser.Translatable.Target.SQLSERVER;

/**
 * Calculate the age in full years.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AgeInYears extends Function {
  public AgeInYears(Structure structure) {
    super("ageinyears", Types.IntType,
          Arrays.asList(new FunctionParameter("s1", Types.DatetimeType),
                        new FunctionParameter("s2", Types.DatetimeType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "extract(year from age(" + args.get(0).translate(target) + ", " +
          args.get(1).translate(target) + "))";
    } else if (target == SQLSERVER) {
      return "case when dateadd(year, datediff(year, " + args.get(1).translate(target) + ", " + args.get(0).translate(target) + "), " + args.get(1).translate(target) + ") > " + args.get(0).translate(target)
           + "  then datediff(hour, " + args.get(1).translate(target) + ", " + args.get(0).translate(target) + ") / 8766 "
           + "  else datediff(hour, " + args.get(1).translate(target) + ", " + args.get(0).translate(target) + ") / 8760 "
           + "end";
    } else {
      return name + '(' + args.get(0).translate(target) + ')';
    }
  }
}