/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.modify;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class InsertRow extends Expression<List<Expression<?, String>>, String> {
  public InsertRow(Context context, List<Expression<?, String>> values) {
    super(context, values);
  }

  public InsertRow(InsertRow other) {
    super(other);
  }

  public InsertRow(InsertRow other, List<Expression<?, String>> value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public InsertRow copy() {
    return new InsertRow(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public InsertRow copy(List<Expression<?, String>> value, T2<String, ? extends Esql<?, ?>>... children) {
    return new InsertRow(this, value, children);
  }

  @Override
  public Type type(EsqlPath path) {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return values().stream()
                   .map(e -> e.translate(target, path.add(e), parameters))
                   .collect(joining(", ", "(", ")"));
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('(');
    boolean first = true;
    for (Expression<?, String> e: values()) {
      if (first) { first = false; }
      else       { st.append(", "); }
      e._toString(st, level, indent);
    }
    st.append(')');
  }

  public List<Expression<?, String>> values() {
    return value;
  }
}
