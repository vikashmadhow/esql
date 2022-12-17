/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec.function;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.database.Structure;
import ma.vi.esql.exec.ExecutionException;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.env.FunctionEnvironment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.macro.Macro;
import ma.vi.esql.syntax.macro.TypedMacro;
import ma.vi.esql.syntax.query.Order;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static ma.vi.base.tuple.T2.of;

/**
 * A function call in ESQL including optional window qualifiers.
 *
 * @author vikash.madhow@gmail.com
 */
public class  FunctionCall extends Expression<String, String> implements TypedMacro {
  public FunctionCall(Context                     context,
                      String                      functionName,
                      boolean                     distinct,
                      List<Expression<?, String>> distinctOn,
                      List<Expression<?, ?>>      arguments,
                      boolean                     star,
                      boolean                     hasWindow,
                      List<Expression<?, String>> partitions,
                      List<Order>                 orderBy,
                      WindowFrame                 frame) {
    super(context, "Call " + functionName,
          of("function",   new Esql<>(context, functionName)),
          of("distinct",   new Esql<>(context, distinct)),
          of("distinctOn", new Esql<>(context, "distinctOn", distinctOn)),
          of("arguments",  new Esql<>(context, "arguments", arguments)),
          of("star",       new Esql<>(context, star)),
          of("hasWindow",  new Esql<>(context, hasWindow)),
          of("partitions", new Esql<>(context, "partitions", partitions)),
          of("orderBy",    new Esql<>(context, "orderBy", orderBy)),
          of("frame",      frame));
  }

  public FunctionCall(Context context,
                      String  functionName,
                      List<Expression<?, ?>> arguments) {
    this(context, functionName, false, null, arguments, false, false, null, null, null);
  }

  public FunctionCall(FunctionCall other) {
    super(other);
  }

  @SafeVarargs
  public FunctionCall(FunctionCall other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public FunctionCall copy() {
    return new FunctionCall(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public FunctionCall copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new FunctionCall(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    Type type = Types.Any;
    List<Expression<?, ?>> arguments = arguments();
    Function function = context.structure.function(functionName());
    if (function != null) {
      type = function.returnType;
      if (type.equals(Types.AsParameterType) && !arguments.isEmpty()) {
        type = arguments.get(0).computeType(path.add(arguments.get(0)));

      } else if (type.equals(Types.AsPromotedNumericParameterType) && !arguments.isEmpty()) {
        type = arguments.get(0).computeType(path.add(arguments.get(0))).promote();
      }
    }
    return type;
  }

  @Override
  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
    String functionName = functionName();
    Structure s = context.structure;
    Function function = s.function(functionName);
    if (function instanceof Macro macro) {
      return macro.expand(esql, path);
    }
    return esql;
  }

  public Function function() {
    Function function = context.structure.function(functionName());
    if (function == null) {
      function = Structure.UnknownFunction;
    }
    return function;
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    String functionName = functionName();
    if (target == Target.JAVASCRIPT) {
      if (functionName.startsWith("$")
       && arguments() != null
       && arguments().size() == 1) {
        /*
         * Special treatment for expressions of the form $x('y') being translated
         * to Javascript: this expression form is used to target the metadata attribute
         * value of a column. E.g., $a('m1') refers to the value of the metadata
         * attribute `m1` of the column `a`. This is translated to Javascript as
         * `row.a.$m.m1`.
         */
        return "row."
             + functionName.substring(1)
             + ".$m."
             + arguments().get(0).exec(target, esqlCon, path, parameters, env);
      } else {
        /*
         * When targeting Javascript, function calls are translated to a form that
         * can be executed on the client-side provided there is an executor object
         * present (assumed to be named `$exec`) available in the execution context
         * and containing the functions invoked as its members.
         *
         * Named arguments, if present, are packaged into an object and provided
         * as the first argument to the javascript function.
         */
        return "(await $exec." + functionName + "({"
              +  arguments().stream  ()
                            .filter  (a -> a instanceof NamedArgument)
                            .map     (a -> a.translate(target, esqlCon, path.add(a), parameters, env).toString())
                            .collect (joining(", ")) + "}"
              + (arguments().stream  ()
                            .anyMatch(a -> !(a instanceof NamedArgument)) ? ", " : "")
              +  arguments().stream  ()
                            .filter  (a -> !(a instanceof NamedArgument))
                            .map     (a -> a.translate(target, esqlCon, path.add(a), parameters, env).toString())
                            .collect (joining(", "))
              + "))";
      }
    } else {
      StringBuilder translation = new StringBuilder(function().translate(this, target, esqlCon, path, parameters, env));

      /*
       * add window suffix
       */
      List<Expression<?, String>> partitions = partitions();
      List<Order> orderBy = orderBy();
      if ((partitions != null && !partitions.isEmpty())
       || (orderBy != null && !orderBy.isEmpty())) {
        boolean overAdded = false;
        if (partitions != null && !partitions.isEmpty()) {
          translation.append(" over (partition by ")
                     .append(partitions.stream()
                                       .map(p -> p.translate(target, esqlCon, path.add(p), parameters, env))
                                       .collect(joining(", ")));
          overAdded = true;
        }
        if (orderBy != null && !orderBy.isEmpty()) {
          translation.append(overAdded ? " " : " over (")
                     .append("order by ")
                     .append(orderBy.stream()
                                    .map(o -> o.translate(target, esqlCon, path.add(o), parameters, env))
                                    .collect(joining(", ")));
        }
        WindowFrame frame = frame();
        if (frame != null) {
          translation.append(' ');
          translation.append(frame.translate(target, esqlCon, path.add(frame), parameters, env));
        }
        translation.append(')');
      }
      return translation.toString();
    }
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(functionName()).append('(');
    if (distinct()) {
      st.append("distinct ");
      if (distinctOn() != null && !distinctOn().isEmpty()) {
        boolean first = true;
        st.append('(');
        for (Expression<?, String> e: distinctOn()) {
          if (first) { first = false; }
          else       { st.append(", "); }
          e._toString(st, level, indent);
        }
        st.append(") ");
      }
    }
    boolean first = true;
    for (Expression<?, ?> e: arguments()) {
      if (first) { first = false; }
      else       { st.append(", "); }
      e._toString(st, level, indent);
    }
    if (star()) {
      st.append('*');
    }
    st.append(')');

    List<Expression<?, String>> partitions = partitions();
    List<Order> orderBy = orderBy();
    if ((partitions != null && !partitions.isEmpty()) ||
        (orderBy != null && !orderBy.isEmpty())) {
      boolean overAdded = false;
      if (partitions != null && !partitions.isEmpty()) {
        st.append(" over (partition by ");
        first = true;
        for (Expression<?, String> e: partitions()) {
          if (first) { first = false; }
          else       { st.append(", "); }
          e._toString(st, level, indent);
        }
        overAdded = true;
      }
      if (orderBy != null && !orderBy.isEmpty()) {
        if (overAdded) {
          st.append(' ');
        } else {
          st.append(" over (");
        }
        st.append("order by ");
        first = true;
        for (Order e: orderBy()) {
          if (first) { first = false; }
          else       { st.append(", "); }
          e._toString(st, level, indent);
        }
      }
      st.append(')');
    }
  }

  @Override
  public Object exec(Target               target,
                     EsqlConnection       esqlCon,
                     EsqlPath             path,
                     PMap<String, Object> parameters,
                     Environment          env) {
    Object f = env.get(functionName());
    if (f instanceof FunctionDecl || f instanceof Function) {
      Environment funcEnv;
      EsqlPath funcPath = path.add(this);
      if (f instanceof FunctionDecl func) {
        /*
         * User-defined function.
         */
        funcEnv = new FunctionEnvironment("Function " + functionName(), func.environment());
        setArgs(this,
                functionName(),
                target,
                new ArrayList<>(func.params()),
                new ArrayList<>(arguments()),
                esqlCon,
                funcPath,
                parameters,
                env,
                funcEnv);

        Object ret = Result.Nothing;
        for (Expression<?, ?> st: func.body()) {
          ret = st.exec(target, esqlCon, funcPath.add(st), parameters, funcEnv);
          if (st instanceof Return) {
            return ret;
          }
        }
        return ret;

      } else {
        /*
         * Internal pre-defined function.
         */
        Function func = (Function)f;
        funcEnv = new FunctionEnvironment("Function " + functionName(), env);
        setArgs(this,
                functionName(),
                target,
                new ArrayList<>(func.parameters()),
                new ArrayList<>(arguments()),
                esqlCon,
                funcPath,
                parameters,
                env,
                funcEnv);
        
        return func.exec(target, esqlCon, funcPath, parameters, funcEnv);
      }
    } else {
      throw new ExecutionException(this, functionName() + " is not a function in "
                                       + "the current environment. It is " + f + '.');
    }
  }

  private static void setArgs(FunctionCall           call,
                              String                 functionName,
                              Target                 target,
                              List<FunctionParam>    params,
                              List<Expression<?, ?>> arguments,
                              EsqlConnection         esqlCon,
                              EsqlPath               path,
                              PMap<String, Object>   parameters,
                              Environment            computeIn,
                              Environment            setIn) {
    if (arguments.size() > params.size()) {
      throw new ExecutionException(call, "Function " + functionName + " take "
                                       + params.size() + " parameters but "
                                       + arguments.size() + " arguments were provided.");
    }

    int i = 0;
    while (i < params.size()) {
      if (i >= arguments.size()) {
        /*
         * If there are more parameters than provided arguments, set the values
         * of the excess parameters to their default value or null.
         */
        FunctionParam param = params.get(i);
        setIn.add(param.name(), param.defaultValue() == null
                              ? null
                              : param.defaultValue().exec(target, esqlCon,
                                                          path.add(param.defaultValue()),
                                                          parameters, computeIn));
        i++;
      } else {
        /*
         * For named arguments match with parameters with the same name; otherwise
         * match to current parameter position and move right.
         */
        Expression<?, ?> arg = arguments.get(i);
        FunctionParam param = null;
        if (arg instanceof NamedArgument na) {
          for (int j = i; j < params.size(); j++) {
            param = params.get(j);
            if (param.name().equals(na.name())) {
              param = params.remove(j);
              break;
            }
          }
          if (param == null) {
            throw new ExecutionException(call,
                "Function " + functionName + " does not take a parameter named "
              + na.name() + " or an argument for the same parameter has been "
              + "specified more than once.");
          }
          arguments.remove(i);
          arg = na.arg();
        } else {
          param = params.get(i);
          i++;
        }
        Object value = arg.exec(target, esqlCon, path.add(arg), parameters, computeIn);
        if (!Types.instanceOf(value, param.type())) {
          throw new ExecutionException(call,
              "Param " + param.name() + " is of type " + param.type() + " but a "
            + "value (" + value + ") of type " + value.getClass().getSimpleName()
            + " was provided.");
        }
        setIn.add(param.name(), value);
      }
    }
  }

  public String functionName() {
    return childValue("function");
  }

  public boolean distinct() {
    return childValue("distinct");
  }

  public List<Expression<?, String>> distinctOn() {
    return child("distinctOn").children();
  }

  public List<Expression<?, ?>> arguments() {
    return child("arguments").children();
  }

  public boolean star() {
    return childValue("star");
  }

  public boolean hasWindow() {
    return childValue("hasWindow");
  }

  public List<Expression<?, String>> partitions() {
    return child("partitions").children();
  }

  public List<Order> orderBy() {
    return child("orderBy").children();
  }

  public WindowFrame frame() {
    return child("frame");
  }
}