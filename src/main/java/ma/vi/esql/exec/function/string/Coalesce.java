/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.string;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import static java.util.Arrays.asList;
import static ma.vi.esql.translation.Translatable.Target.JAVASCRIPT;

/**
 * Coalesce function.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Coalesce extends Function {
  public Coalesce() {
    super("coalesce", Types.AsParameterType,
          asList(new FunctionParam("a", Types.Any),
                 new FunctionParam("b", Types.Any)));
  }

  @Override
  public String translate(FunctionCall         call,
                          Target               target,
                          EsqlConnection       esqlCon,
                          EsqlPath             path,
                          PMap<String, Object> parameters,
                          Environment          env) {
    StringBuilder sb = new StringBuilder();
    if (target == JAVASCRIPT) {
      for (Expression<?, ?> arg: call.arguments()) {
        sb.append(sb.length() == 0 ? "(" : " || ")
          .append(arg.translate(target, esqlCon, path.add(arg), env));
      }
    } else {
      // sql server, esql and all databases
      for (Expression<?, ?> arg: call.arguments()) {
        sb.append(sb.length() == 0 ? "coalesce(" : ", ")
          .append(arg.translate(target, esqlCon, path.add(arg), env));
      }
    }
    return sb.append(')').toString();
  }
}
