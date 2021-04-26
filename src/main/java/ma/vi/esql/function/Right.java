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
 * Right part of a string
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Right extends Function {
  public Right() {
    super("rightstr", Types.IntType,
          asList(new FunctionParameter("s", Types.StringType),
            new FunctionParameter("count", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?, ?>> args = call.arguments();
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
      return "right("
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ')';
    }
  }
}
