/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.debug;

import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.exec.function.FunctionCall;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Prints to console, mainly for debugging purposes.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Print extends Function {
  public Print() {
    super("print", Types.NullType,
          singletonList(new FunctionParam("text", Types.TopType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == JAVASCRIPT) {
      return "console.log(" + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ")";

    } else if (target == SQLSERVER) {
      return "print " + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env);

    } else if (target == POSTGRESQL) {
      return "raise notice " + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env);

    } else {
      /*
       * ESQL and all other databases (not supported in most databases).
       */
      return name + '(' + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ')';
    }
  }

  @Override
  public Object exec(Target target, EsqlConnection esqlCon,
                     EsqlPath path,
                     Environment env) {
    Object text = env.get("text");
    if (text == null) {
      System.out.println("null");
    } else if (text.getClass().isArray()) {
      System.out.println(Arrays.toString((Object[])text));
    } else {
      System.out.println(text);
    }
    return null;
  }
}
