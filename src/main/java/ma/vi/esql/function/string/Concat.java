/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function.string;

import ma.vi.esql.function.Function;
import ma.vi.esql.function.FunctionParameter;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.FunctionCall;
import ma.vi.esql.semantic.type.Types;

import java.util.Iterator;

import static java.util.Arrays.asList;
import static ma.vi.esql.syntax.Translatable.Target.JAVASCRIPT;

/**
 * Concatenates two or more strings.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Concat extends Function {
  public Concat() {
    super("concat", Types.StringType,
          asList(new FunctionParameter("a", Types.StringType),
            new FunctionParameter("b", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    StringBuilder sb = new StringBuilder();
    Iterator<Expression<?, ?>> args = call.arguments().iterator();

    if (target == JAVASCRIPT) {
      sb.append("(").append(args.next().translate(target)).append(").concat(");

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
      sb.append(args.next().translate(target));
    }
    return sb.append(')').toString();
  }
}
