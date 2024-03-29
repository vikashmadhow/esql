/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function.stat;

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
import java.util.Iterator;
import java.util.List;

import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * Given a value and a variadic array of intervals, returns the range
 * where the value fits. E.g.
 * <p>
 * binf(15, 1,5,12,20,35,67) returns '12 to 19'
 * <p>
 * whereas
 * <p>
 * binf(29, 1,5,12,20,35,67) returns '20 to 35'
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class BinFunction extends Function {
  public BinFunction() {
    super("binf", Types.TextType,
          Arrays.asList(new FunctionParam("val", Types.TextType),
            new FunctionParam("ranges", Types.TextType)));
  }

  @Override
  public String translate(FunctionCall call, Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    List<Expression<?, ?>> args = call.arguments();
    Iterator<Expression<?, ?>> i = args.iterator();
    Expression<?, ?> value = i.next();

    if (target == POSTGRESQL) {
      StringBuilder func = new StringBuilder("_core.range((" + value.translate(target, esqlCon, path.add(value), env) + ")::double precision");
      while (i.hasNext()) {
        Expression<?, ?> v = i.next();
        func.append(", (").append(v.translate(target, esqlCon, path.add(v), env)).append(")::int");
      }
      func.append(')');
      return func.toString();

    } else if (target == SQLSERVER) {
      StringBuilder func = new StringBuilder("_core.range(cast(" + value.translate(target, esqlCon, path.add(value), env) + " as float)");
      boolean first = true;
      while (i.hasNext()) {
        if (first) {
          func.append(", '");
          first = false;
        } else {
          func.append(',');
        }
        Expression<?, ?> v = i.next();
        func.append(v.translate(target, esqlCon, path.add(v), env));
      }
      func.append("')");
      return func.toString();

    } else {
      StringBuilder func = new StringBuilder("binf(" + value.translate(target, esqlCon, path.add(value), env));
      while (i.hasNext()) {
        Expression<?, ?> v = i.next();
        func.append(", ").append(v.translate(target, esqlCon, path.add(v), env));
      }
      func.append(')');
      return func.toString();
    }
  }
}