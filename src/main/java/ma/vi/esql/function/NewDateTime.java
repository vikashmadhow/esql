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
 * Function to create a new datetime value from individual components.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NewDateTime extends Function {
  public NewDateTime() {
    super("newdatetime", Types.DatetimeType,
        Arrays.asList(new FunctionParameter("y", Types.IntType),
            new FunctionParameter("m", Types.IntType),
            new FunctionParameter("d", Types.IntType),
            new FunctionParameter("h", Types.IntType),
            new FunctionParameter("mi",Types.IntType),
            new FunctionParameter("s", Types.FloatType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "make_timestamp("
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ", "
          + args.get(2).translate(target) + ", "
          + args.get(3).translate(target) + ", "
          + args.get(4).translate(target) + ", "
          + args.get(5).translate(target) + ')';

    } else if (target == SQLSERVER) {
      // precision and fractional second values must agree in SQL Server
      int fractional = 0;
      int precision = 0;
      String seconds = args.get(5).translate(target);
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
      return "datetime2fromparts("
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ", "
          + args.get(2).translate(target) + ", "
          + args.get(3).translate(target) + ", "
          + args.get(4).translate(target) + ", "
          + seconds + ", "
          + fractional + ", "
          + precision + ')';

    } else {
      return name + '('
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ", "
          + args.get(2).translate(target) + ", "
          + args.get(3).translate(target) + ", "
          + args.get(4).translate(target) + ", "
          + args.get(5).translate(target) + ')';
    }
  }
}