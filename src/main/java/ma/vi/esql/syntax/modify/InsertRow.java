/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.modify;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;
import org.pcollections.PMap;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * A row in an insert statement or dynamic table.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class InsertRow extends Expression<String, String> {
  public InsertRow(Context context, List<Expression<?, String>> values) {
    super(context, "InsertRow", values);
  }

  public InsertRow(InsertRow other) {
    super(other);
  }

  @SafeVarargs
  public InsertRow(InsertRow other, String value, T2<String, ? extends Esql<?, ?>>... children) {
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
  public InsertRow copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new InsertRow(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return values().stream()
                   .map(e -> e.translate(target, esqlCon, path.add(e), parameters, env))
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
    return children();
  }
}
