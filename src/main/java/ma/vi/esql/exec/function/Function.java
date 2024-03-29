/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.scope.Symbol;
import ma.vi.esql.semantic.type.AbstractType;
import ma.vi.esql.semantic.type.Kind;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.translation.Translatable;
import org.pcollections.PMap;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static ma.vi.esql.translation.Translatable.Target.HSQLDB;

/**
 * A stored database function.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Function extends AbstractType implements Symbol {
  public Function(String              name,
                  Type                returnType,
                  List<FunctionParam> parameters) {
    this(name, returnType, parameters, false, null);
  }

  public Function(String               name,
                  Type                 returnType,
                  List<FunctionParam>  parameters,
                  boolean              aggregate,
                  Map<Target, String>  translations) {
    super(name);
    this.returnType = returnType;
    this.parameters = parameters == null ? emptyList() : parameters;
    this.aggregate = aggregate;
    this.translations = translations;
  }

  @Override
  public Function copy() {
    /* Functions are immutable, no need for copy. */
    return this;
  }

  public String translate(FunctionCall         call,
                          Target               target,
                          EsqlConnection       esqlCon,
                          EsqlPath             path,
                          PMap<String, Object> parameters,
                          Environment          env) {
    String functionName = translatedFunctionName(call, target, path);
    StringBuilder st = new StringBuilder(functionName).append('(');
    if (call.distinct()) {
      st.append("distinct ");
      List<Expression<?, String>> distinctOn = call.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        st.append(target != HSQLDB ? "on (" : "(")
          .append(distinctOn.stream()
                            .map(e -> e.translate(target, esqlCon, path.add(e), env))
                            .collect(joining(", ")))
          .append(") ");
      }
    }
    if (call.star()) {
      st.append('*');

    } else if (call.arguments() != null) {
      boolean first = true;
      for (Expression<?, ?> e: call.arguments()) {
        if (first) {
          first = false;
        } else {
          st.append(", ");
        }
        st.append(e.translate(target, esqlCon, path.add(e), env).toString());
      }
    }
    return st.append(')').toString();
  }

  protected String translatedFunctionName(FunctionCall call,
                                          Target       target,
                                          EsqlPath     path) {
    String functionName = translations == null || !translations.containsKey(target)
                        ? name
                        : translations.get(target);
    if (functionName.contains(".")) {
      functionName = Type.dbTableName(functionName, target);
    }
    return functionName;
  }

  public Object exec(Target               target,
                     EsqlConnection       esqlCon,
                     EsqlPath             path,
                     PMap<String, Object> parameters,
                     Environment          env) {
    return null;
  }

  @Override
  public Kind kind() {
    return Kind.FUNCTION;
  }

  @Override
  public boolean isAbstract() {
    return false;
  }

  public List<FunctionParam> parameters() {
    return unmodifiableList(parameters);
  }

  public  final Type returnType;
  private final List<FunctionParam> parameters;
  public  final boolean aggregate;
  public  final Map<Translatable.Target, String> translations;
}
