/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.type.Types;

import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.syntax.Translatable.Target.*;

/**
 * Function to extract part (day, month, year, etc.) of a date.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EndOfMonth extends Function {
  public EndOfMonth() {
    super("endofmonth",
          Types.DateType,
          singletonList(new FunctionParameter("s", Types.DateType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?, ?>> args = call.arguments();
    String arg = args.get(0).translate(target).toString();
    if (target == POSTGRESQL) {
      return "(date_trunc('MONTH', " + arg + ") + INTERVAL '1 MONTH - 1 day')::DATE";

    } else if (target == SQLSERVER) {
      return "eomonth(" + arg + ")";

    } else if (target == JAVASCRIPT) {
      return "(" + arg + ").endOf('month')";
    } else {
      return name + '(' + arg + ')';
    }
  }
}