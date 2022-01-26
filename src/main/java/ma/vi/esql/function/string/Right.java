/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function.string;

import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParameter;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.function.FunctionCall;
import ma.vi.esql.translation.Translatable;

import java.util.List;

import static java.util.Arrays.asList;
import static ma.vi.esql.translation.Translatable.Target.ESQL;
import static ma.vi.esql.translation.Translatable.Target.JAVASCRIPT;

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
  public String translate(FunctionCall call, Translatable.Target target, EsqlPath path) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == JAVASCRIPT) {
      return
          "(" + args.get(0).translate(target, path.add(args.get(0))) + ").substring(("
              + args.get(0).translate(target, path.add(args.get(0))) + ").length - "
              + args.get(1).translate(target, path.add(args.get(1))) + ")";

    } else if (target == ESQL) {
      // ESQL
      return "rightstr("
          + args.get(0).translate(target, path.add(args.get(0))) + ", "
          + args.get(1).translate(target, path.add(args.get(1))) + ')';
    } else {
      // all databases
      return "right("
          + args.get(0).translate(target, path.add(args.get(0))) + ", "
          + args.get(1).translate(target, path.add(args.get(1))) + ')';
    }
  }
}
