/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.string;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.List;

import static java.util.Collections.singletonList;
import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * `expandname` function to expand a column name, such as `first_name` to a more
 *  readable name (e.g., `First name`).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ExpandName extends Function {
  public ExpandName() {
    super("expandname", Types.StringType,
          singletonList(new FunctionParam("name", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall         call,
                          Target               target,
                          EsqlConnection       esqlCon,
                          EsqlPath             path,
                          PMap<String, Object> parameters,
                          Environment          env) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == SQLSERVER) {
      String arg = "trim(replace("
                 + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env)
                 + ", '_', ' '))";
      return "upper(left(" + arg + ", 1)) || "
           + "lower(substring(" + arg + ", 2, len(" + arg + ")-1))";

    } else if (target == JAVASCRIPT) {
      String arg = args.get(0).translate(target, esqlCon, path.add(args.get(0)), env).toString()
                 + ".replaceAll('_', ' ').trim()";
      return "(" + arg + ".substring(0, 1).toUpperCase() + "
                 + arg + ".trim().substring(1).toLowerCase())";

    } else {
      String arg = args.get(0).translate(target, esqlCon, path.add(args.get(0)), env).toString();
      return "upper(left (trim(replace(" + arg + ", '_', ' ')),  1)) || "
           + "lower(right(trim(replace(" + arg + ", '_', ' ')), -1))";
    }
  }
}
