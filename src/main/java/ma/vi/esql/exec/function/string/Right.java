/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.string;

import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

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
    super("right", Types.IntType,
          asList(new FunctionParam("text",  Types.StringType),
                 new FunctionParam("count", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == JAVASCRIPT) {
      return
          "(" + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ").substring(("
              + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ").length - "
              + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ")";

    } else if (target == ESQL) {
      // ESQL
      return "right("
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
          + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ')';
    } else {
      // all databases
      return "right("
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
          + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ')';
    }
  }
}
