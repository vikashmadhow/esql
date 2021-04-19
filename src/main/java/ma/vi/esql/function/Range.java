/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.type.Types;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static ma.vi.esql.parser.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.parser.Translatable.Target.SQLSERVER;

/**
 * Given a value and a variadic array of intervals, returns the range
 * where the value fits. E.g.
 * <p>
 * range(15, 1,5,12,20,35,67) returns '12 to 19'
 * <p>
 * whereas
 * <p>
 * range(29, 1,5,12,20,35,67) returns '20 to 35'
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Range extends Function {
  public Range() {
    super("range", Types.TextType,
          Arrays.asList(new FunctionParameter("val", Types.TextType),
            new FunctionParameter("ranges", Types.TextType)));
  }

  @Override
  public String translate(FunctionCall call, Translatable.Target target) {
    List<Expression<?, ?>> args = call.arguments();
    Iterator<Expression<?, ?>> i = args.iterator();
    Expression<?, ?> value = i.next();

    if (target == POSTGRESQL) {
      StringBuilder func = new StringBuilder("_core.range((" + value.translate(target) + ")::double precision");
      while (i.hasNext()) {
        func.append(", (").append(i.next().translate(target)).append(")::int");
      }
      func.append(')');
      return func.toString();

    } else if (target == SQLSERVER) {
      StringBuilder func = new StringBuilder("_core.range(cast(" + value.translate(target) + " as float)");
      boolean first = true;
      while (i.hasNext()) {
        if (first) {
          func.append(", '");
          first = false;
        } else {
          func.append(',');
        }
        func.append(i.next().translate(target));
      }
      func.append("')");
      return func.toString();

    } else {
      StringBuilder func = new StringBuilder("range(" + value.translate(target));
      while (i.hasNext()) {
        func.append(", ").append(i.next().translate(target));
      }
      func.append(')');
      return func.toString();
    }
  }
}