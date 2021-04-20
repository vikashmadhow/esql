/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.query.Select;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.Map;

/**
 * The exists operator in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Exists extends Expression<Select, String> {
  public Exists(Context context, Select select) {
    super(context, select);
  }

  public Exists(Exists other) {
    super(other);
  }

  @Override
  public Exists copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Exists(this);
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
    return "exists(" + select().translate(target, parameters) + ")";
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append("exists(");
    select()._toString(st, level, indent);
    st.append(')');
  }

  public Select select() {
    return value;
  }
}