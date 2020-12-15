/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.database.Structure;
import ma.vi.esql.type.Types;

import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.parser.Translatable.Target.JAVASCRIPT;
import static ma.vi.esql.parser.Translatable.Target.SQLSERVER;

/**
 * Function returning the length of a string.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class LengthFunction extends Function {
  public LengthFunction(Structure structure) {
    super("length", Types.IntType,
          singletonList(new FunctionParameter("text", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?>> args = call.arguments();
    if (target == SQLSERVER) {
      return "len(" + args.get(0).translate(target) + ')';

    } else if (target == JAVASCRIPT) {
      return "(" + args.get(0).translate(target) + ").length";

    } else {
      // Postgres, ESQL and everything else
      return name + '(' + args.get(0).translate(target) + ')';
    }
  }
}
