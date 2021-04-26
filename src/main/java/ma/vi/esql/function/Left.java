/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.type.Types;

import java.util.List;

import static java.util.Arrays.asList;
import static ma.vi.esql.syntax.Translatable.Target.ESQL;
import static ma.vi.esql.syntax.Translatable.Target.JAVASCRIPT;

/**
 * Left part of a string
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Left extends Function {
  public Left() {
    super("leftstr", Types.StringType,
          asList(new FunctionParameter("s", Types.StringType),
            new FunctionParameter("count", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == JAVASCRIPT) {
      return "(" + args.get(0).translate(target) + ").substring(0"
          + args.get(1).translate(target) + ")";

    } else if (target == ESQL) {
      // ESQL
      return "leftstr("
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ')';
    } else {
      // all databases
      return "left("
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ')';
    }
  }
}
