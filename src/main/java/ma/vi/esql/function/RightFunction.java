/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.type.Types;

import java.util.List;

import static java.util.Arrays.asList;
import static ma.vi.esql.parser.Translatable.Target.ESQL;
import static ma.vi.esql.parser.Translatable.Target.JAVASCRIPT;

/**
 * Right part of a string
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class RightFunction extends Function {
  public RightFunction() {
    super("right", Types.IntType,
          asList(new FunctionParameter("s", Types.StringType),
            new FunctionParameter("count", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?>> args = call.arguments();
    if (target == JAVASCRIPT) {
      return
          "(" + args.get(0).translate(target) + ").substring(("
              + args.get(0).translate(target) + ").length - "
              + args.get(1).translate(target) + ")";

    } else if (target == ESQL) {
      // ESQL
      return "rightstr("
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ')';
    } else {
      // all databases
      return name + '('
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ')';
    }
  }
}
