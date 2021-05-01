/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function.string;

import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParameter;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.semantic.type.Types;

import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.syntax.Translatable.Target.JAVASCRIPT;

/**
 * Function to convert text to upper case.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Upper extends Function {
  public Upper() {
    super("upper", Types.StringType,
          singletonList(new FunctionParameter("text", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == JAVASCRIPT) {
      return "(" + args.get(0).translate(target) + ").toUpperCase()";

    } else {
      // ESQL and all databases
      return name + '(' + args.get(0).translate(target) + ')';
    }
  }
}
