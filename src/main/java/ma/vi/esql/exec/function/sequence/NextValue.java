/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.sequence;

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

/**
 * Function returning the next value from a sequence.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NextValue extends Function {
  public NextValue() {
    super("nextvalue", Types.LongType,
          List.of(new FunctionParam("sequence", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall         call,
                          Target               target,
                          EsqlConnection       esqlCon,
                          EsqlPath             path,
                          PMap<String, Object> parameters,
                          Environment          env) {
    List<Expression<?, ?>> args = call.arguments();
    String sequence = String.valueOf(args.get(0).translate(target, esqlCon, path.add(args.get(0)), env));
    return switch(target) {
      case POSTGRESQL -> "nextval("   + sequence + ")";
      case ESQL       -> "nextvalue(" + sequence + ")";
      case JAVASCRIPT -> "await $exec.nextvalue(" + sequence + ")";
      default         ->  {
        int pos = sequence.indexOf('\'');
        if (pos != -1) {
          sequence = sequence.substring(pos + 1, sequence.length() - 1);
        }
        yield "next value for " + sequence;
      }
    };
  }
}
