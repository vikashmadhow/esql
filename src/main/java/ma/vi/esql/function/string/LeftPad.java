/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function.string;

import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.function.FunctionCall;
import ma.vi.esql.syntax.expression.literal.StringLiteral;
import ma.vi.esql.translation.Translatable;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Function to left pad strings.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LeftPad extends Function {
  public LeftPad() {
    super("lpad", Types.StringType,
          Arrays.asList(new FunctionParam("text",   Types.StringType),
                        new FunctionParam("length", Types.IntType),
                        new FunctionParam("pad",    Types.StringType, false,
                                          new StringLiteral(null, "' '"))));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target, EsqlPath path) {
    List<Expression<?, ?>> args = call.arguments();
    String str = args.get(0).translate(target, path.add(args.get(0))).toString();
    String length = args.get(1).translate(target, path.add(args.get(1))).toString();
    String pad = args.size() > 2 ? args.get(2).translate(target, path.add(args.get(2))).toString() : "' '";

    if (target == JAVASCRIPT) {
      return "(" + str + ").padStart(" + length + ", " + pad + ")";

    } else if (target == POSTGRESQL) {
      return "lpad(" + str + ", " + length + ", " + pad + ')';

    } else if (target == SQLSERVER) {
      return "_core.lpad(" + str + ", " + length + ", " + pad + ')';

    } else {
      // ESQL
      return name + '(' + str + ", " + length + ", " + pad + ')';
    }
  }

  @Override
  public Object exec(EsqlConnection esqlCon,
                     EsqlPath       path,
                     Environment    env) {
    String  text   = env.get("text");
    Long    length = env.get("length");
    String  pad    = env.get("pad");

    return text == null ? null
         : length <= 0  ? text
         : pad == null  ? StringUtils.leftPad(text, length.intValue())
         :                StringUtils.leftPad(text, length.intValue(), pad);
  }
}
