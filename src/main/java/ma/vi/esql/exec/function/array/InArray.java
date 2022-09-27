/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.array;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.Arrays;
import java.util.List;

/**
 * `Function returning true if an element is in an array. It is translated using
 * array operations on databases that have such support or `string_split` on SQL
 * Server.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class InArray extends Function {
  public InArray() {
    super("inarray", Types.BoolType,
          Arrays.asList(new FunctionParam("element", Types.Any),
                        new FunctionParam("array",   Types.AnyArray)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    return switch(target) {
      case POSTGRESQL -> args.get(0).translate(target, esqlCon, path.add(args.get(0)), env)
                      + " in (select * from unnest("
                      + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + "))";

      case SQLSERVER -> args.get(0).translate(target, esqlCon, path.add(args.get(0)), env)
                       + " in (select * from string_split("
                       + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ", '|'))";

      case JAVASCRIPT -> "new Set(" + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ").has("
                                    + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ")";

      default ->  name + '(' + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ", "
                             + args.get(1).translate(target, esqlCon, path.add(args.get(1)), env) + ')';
    };
  }
}
