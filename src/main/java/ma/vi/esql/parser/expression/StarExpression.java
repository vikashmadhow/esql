/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;

import java.util.Map;

/**
 * The star symbol (*) which is only valid as an argument to
 * certain functions such as count.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class StarExpression extends Expression<String> {
  public StarExpression(Context context) {
    super(context, "star");
  }

  public StarExpression(StarExpression other) {
    super(other);
  }

  @Override
  public StarExpression copy() {
    if (!copying()) {
      try {
        copying(true);
        return new StarExpression(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    return "*";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("*");
  }
}
