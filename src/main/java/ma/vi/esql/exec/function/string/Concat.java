/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.string;

import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.exec.function.FunctionCall;

import java.util.Iterator;

import static java.util.Arrays.asList;
import static ma.vi.esql.translation.Translatable.Target.JAVASCRIPT;

/**
 * Concatenates two or more strings.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Concat extends Function {
  public Concat() {
    super("concat", Types.StringType,
          asList(new FunctionParam("a", Types.StringType),
                 new FunctionParam("b", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, Environment env) {
    StringBuilder sb = new StringBuilder();
    Iterator<Expression<?, ?>> args = call.arguments().iterator();

    if (target == JAVASCRIPT) {
      Expression<?, ?> arg = args.next();
      sb.append("(").append(arg.translate(target, esqlCon, path.add(arg), env)).append(").concat(");

    } else {
      // sql server, esql and all databases
      sb.append(name).append('(');
    }

    boolean first = true;
    while (args.hasNext()) {
      if (first) {
        first = false;
      } else {
        sb.append(", ");
      }
      Expression<?, ?> arg = args.next();
      sb.append(arg.translate(target, esqlCon, path.add(arg), env));
    }
    return sb.append(')').toString();
  }
}
