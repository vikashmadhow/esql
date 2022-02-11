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

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Function for finding the index of a string in another string
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class IndexOf extends Function {
  public IndexOf() {
    super("indexof", Types.IntType,
          Arrays.asList(new FunctionParam("text", Types.StringType),
                        new FunctionParam("in",   Types.StringType),
                        new FunctionParam("from", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      if (args.size() > 2) {
        return "strpos("
            + "substring(" + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env)
            + ", " + args.get(2).translate(target, esqlCon, path.add(args.get(2)), env) + "), "
            + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ')';
      } else {
        return "strpos("
            + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", "
            + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ')';
      }
    } else if (target == SQLSERVER) {
      return "charindex("
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
          + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env)
          + (args.size() > 2 ? ", " + args.get(2).translate(target, esqlCon, path.add(args.get(2)), env) : "")
          + ')';
    } else if (target == JAVASCRIPT) {
      return "((" + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ").indexOf("
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env)
          + (args.size() > 2 ? ", " + args.get(2).translate(target, esqlCon, path.add(args.get(2)), env) : "")
          + ") + 1)";
    } else {
      return name + '('
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
          + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env)
          + (args.size() > 2 ? ", " + args.get(2).translate(target, esqlCon, path.add(args.get(2)), env) : "")
          + ')';
    }
  }
}
