/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function.date;

import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParameter;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.translation.Translatable;

import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Function returning the start of the month of the supplied date.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class StartOfMonth extends Function {
  public StartOfMonth() {
    super("startofmonth",
          Types.DateType,
        singletonList(new FunctionParameter("s", Types.DateType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target, EsqlPath path) {
    List<Expression<?, ?>> args = call.arguments();
    String arg = args.get(0).translate(target, path.add(args.get(0))).toString();
    if (target == POSTGRESQL) {
      return "(date_trunc('MONTH', " + arg + ") + INTERVAL '-1 MONTH + 1 day')::DATE";

    } else if (target == SQLSERVER) {
      return "datefromparts(year(" + arg + "), month(" + arg + "), 1)";

    } else if (target == JAVASCRIPT) {
      return "(" + arg + ").startOf('month')";
    } else {
      return name + '(' + arg + ')';
    }
  }
}