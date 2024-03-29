/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

/**
 * The null literal ('null') in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class NullLiteral extends Literal<String> {
  public NullLiteral(Context context) {
    super(context, "null");
  }

  public NullLiteral(NullLiteral other) {
    super(other);
  }

  @SafeVarargs
  public NullLiteral(NullLiteral other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public NullLiteral copy() {
    return new NullLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public NullLiteral copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new NullLiteral(this, value, children);
  }

  @Override
  public Type computeType(EsqlPath path) {
    return Types.NullType;
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return "null";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("null");
  }

  @Override
  public Object exec(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    return null;
  }
}
