/*
 * Copyright (c) 2020 Vikash Madhow
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
  public String translate(FunctionCall call, Translatable.Target target, EsqlPath path) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "extract(" + part.postgresqlName + " from age("
           + args.get(0).translate(target, path.add(args.get(0))) + ", "
           + args.get(1).translate(target, path.add(args.get(1))) + "))";
    } else if (target == SQLSERVER) {
      return "datediff(" + part.mssqlName + ", "
           + args.get(1).translate(target, path.add(args.get(1))) + ", "
           + args.get(0).translate(target, path.add(args.get(0))) + ")";
    } else {
      return name + '(' + args.get(0).translate(target, path.add(args.get(0))) + ')';
    }
  }

  public final DatePart.Part part;
}