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
import static ma.vi.esql.syntax.Translatable.Target.JAVASCRIPT;
import static ma.vi.esql.syntax.Translatable.Target.POSTGRESQL;

/**
 * Extract a substring from the string
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Substring extends Function {
  public Substring() {
    super("substring", Types.StringType,
          asList(new FunctionParameter("s", Types.StringType),
            new FunctionParameter("from", Types.IntType),
            new FunctionParameter("count", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "substr("
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ", "
          + args.get(2).translate(target) + ')';

    } else if (target == JAVASCRIPT) {
      return "(" + args.get(0).translate(target) + ").substring("
          + args.get(1).translate(target) + "-1, "
          + args.get(1).translate(target) + " + " + args.get(2).translate(target) + "-1"
          + ")";

    } else {
      // sql server, esql and all databases
      return name + '('
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target) + ", "
          + args.get(2).translate(target) + ')';
    }
  }
}
