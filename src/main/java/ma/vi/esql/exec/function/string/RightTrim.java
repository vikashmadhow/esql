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

import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.translation.Translatable.Target.JAVASCRIPT;

/**
 * Function to trim string of spaces.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class RightTrim extends Function {
  public RightTrim() {
    super("rtrim", Types.StringType,
          singletonList(new FunctionParam("text", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == JAVASCRIPT) {
      return "(" + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ").trimRight()";

    } else {
      // ESQL and all databases
      return name + '(' + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ')';
    }
  }

  @Override
  public Object exec(Target target, EsqlConnection esqlCon,
                     EsqlPath path,
                     Environment env) {
    String text = env.get("text");
    return text == null ? null : text.stripTrailing();
  }
}
