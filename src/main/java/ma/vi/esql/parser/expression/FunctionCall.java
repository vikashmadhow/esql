/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.database.Structure;
import ma.vi.esql.function.Function;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Macro;
import ma.vi.esql.parser.query.Order;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static ma.vi.base.tuple.T2.of;

/**
 * A function call in ESQL including optional window qualifiers.
 *
 * @author vikash.madhow@gmail.com
 */
public class FunctionCall extends Expression<String> implements Macro {
  public FunctionCall(Context             context,
                      String              functionName,
                      boolean             distinct,
                      List<Expression<?>> distinctOn,
                      List<Expression<?>> arguments,
                      List<Expression<?>> partitions,
                      List<Order>         orderBy) {
    super(context, functionName,
          of("distinct",   new Esql<>(context, distinct)),
          of("distinctOn", new Esql<>(context, "distinctOn", distinctOn)),
          of("arguments",  new Esql<>(context, "arguments", arguments)),
          of("partitions", new Esql<>(context, "partitions", partitions)),
          of("orderBy",    new Esql<>(context, "orderBy", orderBy)));
  }

  public FunctionCall(FunctionCall other) {
    super(other);
  }

  @Override
  public FunctionCall copy() {
    if (!copying()) {
      try {
        copying(true);
        return new FunctionCall(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    Type type = Types.TopType;
    List<Expression<?>> arguments = arguments();
    Function function = context.structure.function(functionName());
    if (function != null) {
      type = function.returnType;
      if (type.equals(Types.AsParameterType) && !arguments.isEmpty()) {
        type = arguments.get(0).type();
      }
    }
    return type;
  }

  @Override
  public boolean expand(String name, Esql<?, ?> esql) {
    String functionName = functionName();
    Structure s = context.structure;
    Function function = s.function(functionName);
    if (function instanceof Macro) {
      Macro macro = (Macro)function;
      return macro.expand(name, this);
    }
    return false;
  }

  @Override
  public String translate(Target target) {
    String functionName = functionName();
    Structure s = context.structure;
    Function function = s.function(functionName);
    if (function == null) {
      function = s.UnknownFunction;
    }
    String translation = function.translate(this, target);

    /*
     * add window suffix
     */
    List<Expression<?>> partitions = partitions();
    List<Order> orderBy = orderBy();
    if ((partitions != null && !partitions.isEmpty()) ||
        (orderBy != null && !orderBy.isEmpty())) {
      boolean overAdded = false;
      if (partitions != null && !partitions.isEmpty()) {
        translation += " over (partition by " +
            partitions.stream()
                      .map(p -> p.translate(target))
                      .collect(joining(", "));
        overAdded = true;
      }
      if (orderBy != null && !orderBy.isEmpty()) {
        if (overAdded) {
          translation += ' ';
        } else {
          translation += " over (";
        }
        translation += "order by " +
            orderBy.stream()
                   .map(o -> o.translate(target))
                   .collect(joining(", "));
      }
      translation += ')';
    }
    return translation;
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(functionName()).append('(');
    if (distinct() != null && distinct()) {
      st.append("distinct ");
      if (distinctOn() != null && !distinctOn().isEmpty()) {
        boolean first = true;
        st.append('(');
        for (Expression<?> e: distinctOn()) {
          if (first) { first = false; }
          else       { st.append(", "); }
          e._toString(st, level, indent);
        }
        st.append(") ");
      }
    }
    boolean first = true;
    for (Expression<?> e: arguments()) {
      if (first) { first = false; }
      else       { st.append(", "); }
      e._toString(st, level, indent);
    }
    st.append(')');

    List<Expression<?>> partitions = partitions();
    List<Order> orderBy = orderBy();
    if ((partitions != null && !partitions.isEmpty()) ||
        (orderBy != null && !orderBy.isEmpty())) {
      boolean overAdded = false;
      if (partitions != null && !partitions.isEmpty()) {
        st.append(" over (partition by ");
        first = true;
        for (Expression<?> e: partitions()) {
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

  public String functionName() {
    return value;
  }

  public Boolean distinct() {
    return childValue("distinct");
  }

  public FunctionCall distinct(Boolean distinct) {
    childValue("distinct", distinct);
    return this;
  }

  public List<Expression<?>> distinctOn() {
    return child("distinctOn").childrenList();
  }

  public FunctionCall distinctOn(List<Expression<?>> on) {
    childrenList("distinctOn", on);
    return this;
  }

  public List<Expression<?>> arguments() {
    return child("arguments").childrenList();
  }

  public List<Expression<?>> partitions() {
    return child("partitions").childrenList();
  }

  public List<Order> orderBy() {
    return child("orderBy").childrenList();
  }
}