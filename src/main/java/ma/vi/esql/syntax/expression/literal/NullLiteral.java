/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.esql.syntax.Context;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;

import java.util.Map;

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

  @Override
  public NullLiteral copy() {
    return new NullLiteral(this);
  }

  @Override
  public Type type() {
    return Types.NullType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return "null";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("null");
  }

  @Override
  public Object value(Translatable.Target target) {
    return null;
  }
}
