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
  public String translate(FunctionCall call, Translatable.Target target, EsqlPath path) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "make_timestamp("
          + args.get(0).translate(target, path.add(args.get(0))) + ", "
          + args.get(1).translate(target, path.add(args.get(1))) + ", "
          + args.get(2).translate(target, path.add(args.get(2))) + ", "
          + args.get(3).translate(target, path.add(args.get(3))) + ", "
          + args.get(4).translate(target, path.add(args.get(4))) + ", "
          + args.get(5).translate(target, path.add(args.get(5))) + ')';

    } else if (target == SQLSERVER) {
      // precision and fractional second values must agree in SQL Server
      int fractional = 0;
      int precision = 0;
      String seconds = args.get(5).translate(target, path.add(args.get(5))).toString();
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
          + args.get(0).translate(target, path.add(args.get(0))) + ", "
          + args.get(1).translate(target, path.add(args.get(1))) + ", "
          + args.get(2).translate(target, path.add(args.get(2))) + ", "
          + args.get(3).translate(target, path.add(args.get(3))) + ", "
          + args.get(4).translate(target, path.add(args.get(4))) + ", "
          + seconds + ", "
          + fractional + ", "
          + precision + ')';

    } else {
      return name + '('
          + args.get(0).translate(target, path.add(args.get(0))) + ", "
          + args.get(1).translate(target, path.add(args.get(1))) + ", "
          + args.get(2).translate(target, path.add(args.get(2))) + ", "
          + args.get(3).translate(target, path.add(args.get(3))) + ", "
          + args.get(4).translate(target, path.add(args.get(4))) + ", "
          + args.get(5).translate(target, path.add(args.get(5))) + ')';
    }
  }
}