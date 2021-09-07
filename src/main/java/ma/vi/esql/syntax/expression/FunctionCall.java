/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Structure;
import ma.vi.esql.function.Function;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Macro;
import ma.vi.esql.syntax.query.Order;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static ma.vi.base.tuple.T2.of;

/**
 * A function call in ESQL including optional window qualifiers.
 *
 * @author vikash.madhow@gmail.com
 */
public class FunctionCall extends Expression<String, String> implements Macro {
  public FunctionCall(Context             context,
                      String              functionName,
                      boolean             distinct,
                      List<Expression<?, String>> distinctOn,
                      List<Expression<?, ?>> arguments,
                      boolean             star,
                      List<Expression<?, String>> partitions,
                      List<Order>         orderBy) {
    super(context, functionName,
          of("distinct",   new Esql<>(context, distinct)),
          of("distinctOn", new Esql<>(context, "distinctOn", distinctOn)),
          of("arguments",  new Esql<>(context, "arguments", arguments)),
          of("star",       new Esql<>(context, star)),
          of("partitions", new Esql<>(context, "partitions", partitions)),
          of("orderBy",    new Esql<>(context, "orderBy", orderBy)));
  }

  public FunctionCall(FunctionCall other) {
    super(other);
  }

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
  public Type type() {
    Type type = Types.TopType;
    List<Expression<?, ?>> arguments = arguments();
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
  public Esql<?, ?> expand(Esql<?, ?> esql, EsqlPath path) {
    String functionName = functionName();
    Structure s = context.structure;
    Function function = s.function(functionName);
    if (function instanceof Macro macro) {
      return macro.expand(esql, path);
    }
    return esql;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
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
    List<Expression<?, String>> partitions = partitions();
    List<Order> orderBy = orderBy();
    if ((partitions != null && !partitions.isEmpty()) ||
        (orderBy != null && !orderBy.isEmpty())) {
      boolean overAdded = false;
      if (partitions != null && !partitions.isEmpty()) {
        translation += " over (partition by " +
            partitions.stream()
                      .map(p -> p.translate(target, path.add(p), parameters))
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
                   .map(o -> o.translate(target, path.add(o), parameters))
                   .collect(joining(", "));
      }
      translation += ')';
    }
    return translation;
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

  public String functionName() {
    return value;
  }

  public boolean distinct() {
    return childValue("distinct");
  }

  public FunctionCall distinct(boolean distinct) {
    childValue("distinct", distinct);
    return this;
  }

  public List<Expression<?, String>> distinctOn() {
    return child("distinctOn").children();
  }

  public FunctionCall distinctOn(List<Expression<?, String>> on) {
    childrenList("distinctOn", on);
    return this;
  }

  public List<Expression<?, ?>> arguments() {
    return child("arguments").children();
  }

  public boolean star() {
    return childValue("star");
  }

  public List<Expression<?, String>> partitions() {
    return child("partitions").children();
  }

  public List<Order> orderBy() {
    return child("orderBy").children();
  }
}