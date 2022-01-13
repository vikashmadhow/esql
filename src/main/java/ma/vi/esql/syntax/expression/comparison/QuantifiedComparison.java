/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.comparison;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.query.Select;

import java.util.Map;

/**
 * The quantified comparison operator in ESQL takes the form of
 * <pre>
 *   expression comparison-operator ('all'|'any') (select)
 * </pre>
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class QuantifiedComparison extends Expression<String, String> {
  public QuantifiedComparison(Context               context,
                              Expression<?, String> expr,
                              String                compareOp,
                              String                quantifier,
                              Select                select) {
    super(context, "QuantifiedComparison",
          T2.of("expr", expr),
          T2.of("compareOp", new Esql<>(context, compareOp)),
          T2.of("quantifier", new Esql<>(context, quantifier)),
          T2.of("select", select));
  }

  public QuantifiedComparison(QuantifiedComparison other) {
    super(other);
  }

  @SafeVarargs
  public QuantifiedComparison(QuantifiedComparison other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public QuantifiedComparison copy() {
    return new QuantifiedComparison(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public QuantifiedComparison copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new QuantifiedComparison(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return expr().translate(target, path.add(expr()), parameters) + ' ' + compareOp() + ' ' + quantifier()
         + select().translate(target, path.add(select()), parameters);
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    expr()._toString(st, level, indent);
    st.append(' ').append(compareOp()).append(' ').append(quantifier());
    select()._toString(st, level, indent);
  }

  public Expression<?, String> expr() {
    return childValue("expr");
  }

  public String compareOp() {
    return childValue("compareOp");
  }

  public String quantifier() {
    return childValue("quantifier");
  }

  public Select select() {
    return child("select");
  }
}
