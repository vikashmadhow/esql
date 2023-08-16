/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.array;

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

/**
 * Function returning true if the two array overlaps, i.e., their intersection
 * is not empty.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class IntersectArray extends Function {
  public IntersectArray() {
    super("intersect", Types.BoolType,
          Arrays.asList(new FunctionParam("array1", Types.AnyArray),
                        new FunctionParam("array2", Types.AnyArray)));
  }

  @Override
  public String translate(FunctionCall         call,
                          Target               target,
                          EsqlConnection       esqlCon,
                          EsqlPath             path,
                          PMap<String, Object> parameters,
                          Environment          env) {
    List<Expression<?, ?>> args = call.arguments();
    String first  = args.get(0).translate(target, esqlCon, path.add(args.get(0)), env).toString();
    String second = args.get(0).translate(target, esqlCon, path.add(args.get(0)), env).toString();
    return switch(target) {
      case POSTGRESQL -> first + " && " + second;

      case SQLSERVER -> "(select count(*) c from ("
                      + " select a.value v1, b.value v2"
                      + "   from string_split(" + first  + ", '|') a "
                      + "   join string_split(" + second + ", '|') b on a.value = b.value) x) > 0";

      case JAVASCRIPT -> first + ".some(v => " + second + ".includes(v))";

      default -> name + '(' + first + ", " + second + ')';
    };
  }
}
