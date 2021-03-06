/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.type.Types;

import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.parser.Translatable.Target.JAVASCRIPT;

/**
 * Function to convert text to upper case.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class UpperFunction extends Function {
  public UpperFunction() {
    super("upper", Types.StringType,
          singletonList(new FunctionParameter("text", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?>> args = call.arguments();
    if (target == JAVASCRIPT) {
      return "(" + args.get(0).translate(target) + ").toUpperCase()";

    } else {
      // ESQL and all databases
      return name + '(' + args.get(0).translate(target) + ')';
    }
  }
}
