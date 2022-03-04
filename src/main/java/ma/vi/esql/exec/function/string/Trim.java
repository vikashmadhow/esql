/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.string;

import ma.vi.esql.exec.EsqlConnection;
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
import static ma.vi.esql.translation.Translatable.Target.JAVASCRIPT;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Function to trim string of spaces.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Trim extends Function {
  public Trim() {
    super("trim", Types.StringType,
          singletonList(new FunctionParam("text", Types.StringType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    if (target == JAVASCRIPT) {
      return "(" + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ").trim()";

    } else if (target == SQLSERVER) {
      /*
       * default trim only removes space (char 32) in SQL Server, we use the
       * special form to specify all other space characters that we want to
       * remove.
       */
      return name + "(nchar(0x09) + nchar(0x20) + nchar(0x0D) + nchar(0x0A) from "
          + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ')';
    } else {
      /*
       * ESQL and all other databases
       */
      return name + '(' + args.get(0).translate(target, esqlCon, path.add(args.get(0)), env) + ')';
    }
  }

  @Override
  public Object exec(Target target, EsqlConnection esqlCon,
                     EsqlPath path,
                     PMap<String, Object> parameters, Environment env) {
    String text = env.get("text");
    return text == null ? null : text.trim();
  }
}
