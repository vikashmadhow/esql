/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.date;

import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.exec.function.FunctionCall;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Function to create a new datetime value from individual components.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NewDateTime extends Function {
  public NewDateTime() {
    super("newdatetime", Types.DatetimeType,
        Arrays.asList(new FunctionParam("y", Types.IntType),
            new FunctionParam("m", Types.IntType),
            new FunctionParam("d", Types.IntType),
            new FunctionParam("h", Types.IntType),
            new FunctionParam("mi", Types.IntType),
            new FunctionParam("s", Types.FloatType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "make_timestamp("
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
          + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", "
          + args.get(2).translate(target, esqlCon, path.add(args.get(2)), env) + ", "
          + args.get(3).translate(target, esqlCon, path.add(args.get(3)), env) + ", "
          + args.get(4).translate(target, esqlCon, path.add(args.get(4)), env) + ", "
          + args.get(5).translate(target, esqlCon, path.add(args.get(5)), env) + ')';

    } else if (target == SQLSERVER) {
      // precision and fractional second values must agree in SQL Server
      int fractional = 0;
      int precision = 0;
      String seconds = args.get(5).translate(target, esqlCon, path.add(args.get(5)), env).toString();
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
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
          + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", "
          + args.get(2).translate(target, esqlCon, path.add(args.get(2)), env) + ", "
          + args.get(3).translate(target, esqlCon, path.add(args.get(3)), env) + ", "
          + args.get(4).translate(target, esqlCon, path.add(args.get(4)), env) + ", "
          + seconds + ", "
          + fractional + ", "
          + precision + ')';

    } else {
      return name + '('
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
          + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", "
          + args.get(2).translate(target, esqlCon, path.add(args.get(2)), env) + ", "
          + args.get(3).translate(target, esqlCon, path.add(args.get(3)), env) + ", "
          + args.get(4).translate(target, esqlCon, path.add(args.get(4)), env) + ", "
          + args.get(5).translate(target, esqlCon, path.add(args.get(5)), env) + ')';
    }
  }
}