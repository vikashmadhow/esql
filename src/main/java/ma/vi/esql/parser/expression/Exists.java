/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.query.Select;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

public class Exists extends Expression<Select> {
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
  public String translate(Target target) {
    return "exists(" + select().translate(target) + ")";
  }

  public Select select() {
    return value;
  }
}