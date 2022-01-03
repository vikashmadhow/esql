/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.function.date;

import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParameter;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.syntax.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.syntax.Translatable.Target.SQLSERVER;

/**
 * Returns the number of months between 2 dates, adding one month for any remaining
 * days in the period, analogous to computing the ceiling of a fractional number.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class MonthsCeiling extends Function {
  public MonthsCeiling() {
    super("monthsceil", Types.IntType,
          Arrays.asList(new FunctionParameter("s1",Types.DatetimeType),
                        new FunctionParameter("s2",Types.DatetimeType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target, EsqlPath path) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return """
          extract(month from age(%1$s, %2$s)) \
          + case when extract(day from %1$s + concat(extract(month from age(%1$s, %2$s)), 'month')::interval) <= 0 then 0 else 1 end
          """.formatted(args.get(0).translate(target),
                        args.get(1).translate(target));
    } else if (target == SQLSERVER) {
      return """
          datediff(month, %2$s, %1$s) \
          + iif(datediff(day, dateadd(month, datediff(month, %2$s, %1$s), %2$s), %1$s) <= 0, 0, 1)
          """.formatted(args.get(0).translate(target),
                        args.get(1).translate(target));
    } else {
      return name + '(' + args.get(0).translate(target) + ')';
    }
  }
}