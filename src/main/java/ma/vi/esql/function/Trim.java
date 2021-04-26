/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.type.Types;

import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.syntax.Translatable.Target.JAVASCRIPT;
import static ma.vi.esql.syntax.Translatable.Target.SQLSERVER;

/**
 * Function to trim string of spaces.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Trim extends Function {
  public Trim() {
    super("trim", Types.StringType,
        singletonList(new FunctionParameter("text", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == JAVASCRIPT) {
      return "(" + args.get(0).translate(target) + ").trim()";

    } else if (target == SQLSERVER) {
      /*
       * default trim only removes space (char 32) in SQL Server, we use the
       * special form to specify all other space characters that we want to
       * remove.
       */
      return name + "(nchar(0x09) + nchar(0x20) + nchar(0x0D) + nchar(0x0A) from "
          + args.get(0).translate(target) + ')';
    } else {
      // ESQL and all other databases
      return name + '(' + args.get(0).translate(target) + ')';
    }
  }
}
