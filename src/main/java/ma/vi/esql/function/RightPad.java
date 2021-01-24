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

import static ma.vi.esql.parser.Translatable.Target.*;

/**
 * Function to right pad strings.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class RightPad extends Function {
  public RightPad() {
    super("rpad", Types.StringType,
          Arrays.asList(new FunctionParameter("str", Types.StringType),
            new FunctionParameter("length", Types.IntType),
            new FunctionParameter("pad", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?>> args = call.arguments();
    String str = args.get(0).translate(target);
    String length = args.get(1).translate(target);
    String pad = args.size() > 2 ? args.get(2).translate(target) : "' '";

    if (target == JAVASCRIPT) {
      return "(" + str + ").padEnd(" + length + ", " + pad + ")";

    } else if (target == POSTGRESQL) {
      return "rpad(" + str + ", " + length + ", " + pad + ')';

    } else if (target == SQLSERVER) {
      return "_core.rpad(" + str + ", " + length + ", " + pad + ')';

    } else {
      // ESQL
      return name + '(' + str + ", " + length + ", " + pad + ')';
    }
  }
}
