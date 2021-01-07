/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.function;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.parser.query.QueryTranslation;
import ma.vi.esql.parser.query.Select;
import ma.vi.esql.type.Type;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static ma.vi.esql.parser.Translatable.Target.HSQLDB;

/**
 * A stored database function.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Function {
  public Function(String name,
                  Type returnType,
                  List<FunctionParameter> parameters) {
    this(name, returnType, parameters, false, null);
  }

  public Function(String name,
                  Type returnType,
                  List<FunctionParameter> parameters,
                  boolean aggregate,
                  Map<Translatable.Target, String> translations) {
    this.name = name;
    this.returnType = returnType;
    this.parameters = parameters == null ? emptyList() : parameters;
    this.aggregate = aggregate;
    this.translations = translations;
  }

  public String translate(FunctionCall call, Translatable.Target target) {
    String functionName = translations == null || !translations.containsKey(target) ? name : translations.get(target);
    if (functionName.contains(".")) {
      functionName = Type.dbTableName(functionName, target);
    }
    StringBuilder st = new StringBuilder(functionName).append('(');
    if (call.distinct()) {
      st.append("distinct ");
      List<Expression<?>> distinctOn = call.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        st.append(target != HSQLDB ? "on (" : "(")
          .append(distinctOn.stream()
                            .map(e -> e.translate(target))
                            .collect(joining(", ")))
          .append(") ");
      }
    }
    Select select = call.select();
    if (select != null) {
      st.append(select.translate(target).statement);

    } else if (call.star()) {
      st.append('*');

    } else if (call.arguments() != null) {
      boolean first = true;
      for (Expression<?> e: call.arguments()) {
        if (first) {
          first = false;
        } else {
          st.append(", ");
        }
        st.append(e.translate(target));
      }
    }
    return st.append(')').toString();
  }

  public final String name;
  public final Type returnType;
  public final List<FunctionParameter> parameters;
  public final boolean aggregate;
  public final Map<Translatable.Target, String> translations;
}
