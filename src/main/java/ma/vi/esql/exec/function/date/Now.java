/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.date;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

import static java.util.Collections.emptyList;
import static ma.vi.esql.translation.Translatable.Target.JAVASCRIPT;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Function to create a new datetime value from individual components.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Now extends Function {
  public Now() {
    super("now", Types.DatetimeType, emptyList());
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return target == SQLSERVER  ? "getdate()"
         : target == JAVASCRIPT ? "new Date()"
         : "now()";
  }
}