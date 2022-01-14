/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function.string;

import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParameter;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.translation.Translatable;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.*;

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
  public String translate(FunctionCall call, Translatable.Target target, EsqlPath path) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == POSTGRESQL) {
      if (args.size() > 2) {
        return "strpos("
            + "substring(" + args.get(1).translate(target, path.add(args.get(1)))
            + ", " + args.get(2).translate(target, path.add(args.get(2))) + "), "
            + args.get(0).translate(target, path.add(args.get(0))) + ')';
      } else {
        return "strpos("
            + args.get(1).translate(target, path.add(args.get(1))) + ", "
            + args.get(0).translate(target, path.add(args.get(0))) + ')';
      }
    } else if (target == SQLSERVER) {
      return "charindex("
          + args.get(0).translate(target, path.add(args.get(0))) + ", "
          + args.get(1).translate(target, path.add(args.get(1)))
          + (args.size() > 2 ? ", " + args.get(2).translate(target, path.add(args.get(2))) : "")
          + ')';
    } else if (target == JAVASCRIPT) {
      return "((" + args.get(1).translate(target, path.add(args.get(1))) + ").indexOf("
          + args.get(0).translate(target, path.add(args.get(0)))
          + (args.size() > 2 ? ", " + args.get(2).translate(target, path.add(args.get(2))) : "")
          + ") + 1)";
    } else {
      return name + '('
          + args.get(0).translate(target, path.add(args.get(0))) + ", "
          + args.get(1).translate(target, path.add(args.get(1)))
          + (args.size() > 2 ? ", " + args.get(2).translate(target, path.add(args.get(2))) : "")
          + ')';
    }
  }
}
