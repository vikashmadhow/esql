/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.database.Structure;
import ma.vi.esql.type.Types;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.parser.Translatable.Target.*;

/**
 * Function to left pad strings.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LeftPad extends Function {
  public LeftPad(Structure structure) {
    super("lpad", Types.TextType,
          Arrays.asList(new FunctionParameter("str", Types.TextType),
            new FunctionParameter("length", Types.IntType),
            new FunctionParameter("pad", Types.TextType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?>> args = call.arguments();
    String str = args.get(0).translate(target);
    String length = args.get(1).translate(target);
    String pad = args.size() > 2 ? args.get(2).translate(target) : "' '";

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
}
