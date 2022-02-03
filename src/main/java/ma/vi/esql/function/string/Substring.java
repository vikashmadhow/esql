/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function.string;

import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.function.FunctionCall;
import ma.vi.esql.translation.Translatable;

import java.util.List;

import static java.util.Arrays.asList;
import static ma.vi.esql.translation.Translatable.Target.JAVASCRIPT;
import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;

/**
 * Extract a substring from the string
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Substring extends Function {
  public Substring() {
    super("substring", Types.StringType,
          asList(new FunctionParam("text", Types.StringType),
                 new FunctionParam("from", Types.IntType),
                 new FunctionParam("count", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target, EsqlPath path) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      return "substr("
          + args.get(0).translate(target, path.add(args.get(0))) + ", "
          + args.get(1).translate(target, path.add(args.get(1))) + ", "
          + args.get(2).translate(target, path.add(args.get(2))) + ')';

    } else if (target == JAVASCRIPT) {
      return "(" + args.get(0).translate(target, path.add(args.get(0))) + ").substring("
          + args.get(1).translate(target, path.add(args.get(1))) + "-1, "
          + args.get(1).translate(target, path.add(args.get(1))) + " + " + args.get(2).translate(target, path.add(args.get(2))) + "-1"
          + ")";

    } else {
      // sql server, esql and all databases
      return name + '('
          + args.get(0).translate(target, path.add(args.get(0))) + ", "
          + args.get(1).translate(target, path.add(args.get(1))) + ", "
          + args.get(2).translate(target, path.add(args.get(2))) + ')';
    }
  }
}
