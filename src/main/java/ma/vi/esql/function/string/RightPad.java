/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function.string;

import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.function.FunctionCall;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Function to right pad strings.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class RightPad extends Function {
  public RightPad() {
    super("rpad", Types.StringType,
          Arrays.asList(new FunctionParam("text",   Types.StringType),
                        new FunctionParam("length", Types.IntType),
                        new FunctionParam("pad",    Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    String str = args.get(0).translate(target, esqlCon, path.add(args.get(0)), env).toString();
    String length = args.get(1).translate(target, esqlCon, path.add(args.get(1)), env).toString();
    String pad = args.size() > 2 ? args.get(2).translate(target, esqlCon, path.add(args.get(2)), env).toString() : "' '";

    if (target == JAVASCRIPT) {
      return "(" + str + ").padEnd(" + length + ", " + pad + ")";

    } else if (target == POSTGRESQL) {
      return "rpad(" + str + ", " + length + ", " + pad + ')';

    } else if (target == SQLSERVER) {
      return "_core.rpad(" + str + ", " + length + ", " + pad + ')';

    } else {
      // ESQL
      return name + '(' + str + ", " + length + ", " + pad + ')';
    }
  }

  @Override
  public Object exec(Target target, EsqlConnection esqlCon,
                     EsqlPath path,
                     Environment env) {
    String  text   = env.get("text");
    Long    length = env.get("length");
    String  pad    = env.get("pad");

    return text == null ? null
         : length <= 0  ? text
         : pad == null  ? StringUtils.rightPad(text, length.intValue())
         :                StringUtils.rightPad(text, length.intValue(), pad);
  }
}
