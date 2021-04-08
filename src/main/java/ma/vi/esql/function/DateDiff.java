/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.type.Types;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.parser.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.parser.Translatable.Target.SQLSERVER;

/**
 * Function to calculate difference between two dates.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DateDiff extends Function {
  public DateDiff(String name, DatePart.Part part) {
    super(name, Types.IntType,
          Arrays.asList(new FunctionParameter("s1", Types.DatetimeType),
            new FunctionParameter("s2", Types.DatetimeType)));
    this.part = part;
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "extract(" + part.postgresqlName + " from age(" + args.get(0).translate(target) + ", " +
          args.get(1).translate(target) + "))";
    } else if (target == SQLSERVER) {
      return "datediff(" + part.mssqlName + ", " + args.get(1).translate(target) + ", " +
          args.get(0).translate(target) + ")";
    } else {
      return name + '(' + args.get(0).translate(target) + ')';
    }
  }

  public final DatePart.Part part;
}