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

import static java.util.Collections.singletonList;
import static ma.vi.esql.translation.Translatable.Target.JAVASCRIPT;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Function returning the length of a string.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Length extends Function {
  public Length() {
    super("length", Types.IntType,
          singletonList(new FunctionParam("text", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == SQLSERVER) {
      return "len(" + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ')';

    } else if (target == JAVASCRIPT) {
      return "(" + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ").length";

    } else {
      // Postgres, ESQL and everything else
      return name + '(' + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ')';
    }
  }
}
