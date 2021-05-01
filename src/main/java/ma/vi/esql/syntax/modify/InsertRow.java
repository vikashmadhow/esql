/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.modify;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;

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

  @Override
  public InsertRow copy() {
    if (!copying()) {
      try {
        copying(true);
        return new InsertRow(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.BoolType;
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    return values().stream()
                   .map(e -> e.translate(target, parameters))
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
