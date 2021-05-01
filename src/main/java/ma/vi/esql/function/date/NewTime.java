/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function.date;

import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParameter;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.semantic.type.Types;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.syntax.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.syntax.Translatable.Target.SQLSERVER;

/**
 * Function to create a new time value from individual components.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NewTime extends Function {
  public NewTime() {
    super("newtime", Types.TimeType,
          Arrays.asList(new FunctionParameter("h", Types.IntType),
            new FunctionParameter("mi", Types.IntType),
            new FunctionParameter("s", Types.FloatType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "make_time("
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ", "
          + args.get(2).translate(target) + ')';

    } else if (target == SQLSERVER) {
      // precision and fractional second values must agree in SQL Server
      int fractional = 0;
      int precision = 0;
      String seconds = args.get(5).translate(target).toString();
      int pos = seconds.indexOf('.');
      if (pos != -1) {
        String fraction = seconds.substring(pos + 1);
        seconds = seconds.substring(0, pos);

        // drop trailing zeroes
        int i = fraction.length();
        while (i > 0 && fraction.charAt(i - 1) == '0') i--;
        fraction = fraction.substring(0, i);

        fractional = Integer.parseInt(fraction);
        precision = fraction.length();
      }
      return "timefromparts("
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ", "
          + seconds + ", "
          + fractional + ", "
          + precision + ')';

    } else {
      return name + '('
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ", "
          + args.get(2).translate(target) + ')';
    }
  }
}