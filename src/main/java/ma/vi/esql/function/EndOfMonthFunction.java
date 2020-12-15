/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.database.Structure;
import ma.vi.esql.type.Types;

import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.parser.Translatable.Target.*;

/**
 * Function to extract part (day, month, year, etc.) of a date.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EndOfMonthFunction extends Function {
  public EndOfMonthFunction(Structure structure) {
    super("endofmonth",
          Types.DateType,
          singletonList(new FunctionParameter("s", Types.DateType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?>> args = call.arguments();
    String arg = args.get(0).translate(target);
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