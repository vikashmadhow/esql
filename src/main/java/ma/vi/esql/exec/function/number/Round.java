/*
 * Copyright (c) 2023 Vikash Madhow
 */

package ma.vi.esql.exec.function.number;

import ma.vi.base.util.Numbers;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.Arrays;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Function to round numbers.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Round extends Function {
  public Round() {
    super("round", Types.DoubleType,
          Arrays.asList(new FunctionParam("number",   Types.NumberType),
                        new FunctionParam("decimals", Types.IntType)));
  }

  @Override
  public String translate(FunctionCall         call,
                          Target               target,
                          EsqlConnection       esqlCon,
                          EsqlPath             path,
                          PMap<String, Object> parameters,
                          Environment          env) {
    List<Expression<?, ?>> args = call.arguments();
    String number = args.get(0).translate(target,
                                          esqlCon,
                                          path.add(args.get(0)),
                                          env).toString();
    String fractions = args.size() > 1
                     ? args.get(1).translate(target,
                                             esqlCon,
                                             path.add(args.get(1)),
                                             env).toString()
                     : null;
    if (target == JAVASCRIPT) {
      return fractions == null
           ? "Math.round("  + number + ")"
           : "parseFloat((" + number + ").toFixed(" + fractions + "))";

    } else if (target == POSTGRESQL) {
      return fractions == null
           ? "round("  + number + ')'
           : "round((" + number + ")::numeric, " + fractions + ")";

    } else if (target == SQLSERVER) {
      return fractions == null
           ? "round(" + number + ", 0)"
           : "round(" + number + ", " + fractions + ")";

    } else {
      // ESQL
      return fractions == null
           ? "round(" + number + ')'
           : "round(" + number + ", " + fractions + ")";
    }
  }

  @Override
  public Object exec(Target               target,
                     EsqlConnection       esqlCon,
                     EsqlPath             path,
                     PMap<String, Object> parameters,
                     Environment          env) {
    Double  number   = env.get("number");
    Integer decimals = env.get("decimals");
    return number == null
         ? null
         : Numbers.round(number, decimals == null ? 0 : decimals);
  }
}
