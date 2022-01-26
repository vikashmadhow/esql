/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function.string;

import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParameter;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.function.FunctionCall;
import ma.vi.esql.translation.Translatable;

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
          singletonList(new FunctionParameter("text", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target, EsqlPath path) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == SQLSERVER) {
      return "len(" + args.get(0).translate(target, path.add(args.get(0))) + ')';

    } else if (target == JAVASCRIPT) {
      return "(" + args.get(0).translate(target, path.add(args.get(0))) + ").length";

    } else {
      // Postgres, ESQL and everything else
      return name + '(' + args.get(0).translate(target, path.add(args.get(0))) + ')';
    }
  }
}
