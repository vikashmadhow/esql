/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;
import static ma.vi.esql.syntax.Translatable.Target.JSON;

/**
 * Parent of binary operators in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class BinaryOperator extends Expression<String, String>  {
  public BinaryOperator(Context context,
                        String op,
                        Expression<?, ?> expr1,
                        Expression<?, ?> expr2) {
    super(context, "BinaryOp",
          T2.of("op", new Esql<>(context, op)),
          T2.of("expr1", expr1),
          T2.of("expr2", expr2));

  }

  public BinaryOperator(BinaryOperator other) {
    super(other);
  }

  @SafeVarargs
  public BinaryOperator(BinaryOperator other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract BinaryOperator copy();

  @Override
  public abstract BinaryOperator copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  @Override
  public Type type(EsqlPath path) {
    return expr1().type(path.add(expr1()));
  }

  @Override
  protected String trans(Target target,
                         EsqlPath path,
                         Map<String, Object> parameters) {
    String e = expr1().translate(target, path.add(expr1()), parameters)
             + ' ' + op() + ' '
             + expr2().translate(target, path.add(expr2()), parameters);
    return target == JSON ? '"' + escapeJsonString(e) + '"' : e;
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr1()._toString(st, level, indent);
    st.append(' ').append(op()).append(' ');
    expr2()._toString(st, level, indent);
  }

  public String op() {
    return childValue("op");
  }

  public Expression<?, String> expr1() {
    return child("expr1");
  }

  public Expression<?, String> expr2() {
    return child("expr2");
  }
}
