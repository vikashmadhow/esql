/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.type.Types;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.syntax.Translatable.Target.*;

/**
 * Function for finding the index of a string in another string
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class IndexOf extends Function {
  public IndexOf() {
    super("indexof", Types.IntType,
          Arrays.asList(new FunctionParameter("substr_to_find", Types.StringType),
            new FunctionParameter("str_to_search", Types.StringType),
            new FunctionParameter("start_position_optional", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      if (args.size() > 2) {
        return "strpos("
            + "substring(" + args.get(1).translate(target) + ", " + args.get(2).translate(target) + "), "
            + args.get(0).translate(target) + ')';
      } else {
        return "strpos("
            + args.get(1).translate(target) + ", "
            + args.get(0).translate(target) + ')';
      }
    } else if (target == SQLSERVER) {
      return "charindex("
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target)
          + (args.size() > 2 ? ", " + args.get(2).translate(target) : "")
          + ')';
    } else if (target == JAVASCRIPT) {
      return "((" + args.get(1).translate(target) + ").indexOf("
          + args.get(0).translate(target)
          + (args.size() > 2 ? ", " + args.get(2).translate(target) : "")
          + ") + 1)";
    } else {
      return name + '('
          + args.get(0).translate(target) + ", "
          + args.get(1).translate(target)
          + (args.size() > 2 ? ", " + args.get(2).translate(target) : "")
          + ')';
    }
  }
}
