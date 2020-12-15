/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

public class NullLiteral extends Literal<String> {
  public NullLiteral(Context context) {
    super(context, "null");
  }

  public NullLiteral(NullLiteral other) {
    super(other);
  }

  @Override
  public NullLiteral copy() {
    if (!copying()) {
      try {
        copying(true);
        return new NullLiteral(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.NullType;
  }

  @Override
  public String translate(Target target) {
    return "null";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("null");
  }

  @Override
  public Object value(Target target) {
    return null;
  }
}
