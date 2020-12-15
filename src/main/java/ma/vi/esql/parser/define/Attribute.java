/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.expression.Expression;

public class Attribute extends Esql<String, String> {
  public Attribute(Context context,
                   String name,
                   Expression<?> value) {
    super(context, name, T2.of("value", value));
  }

  public Attribute(Attribute other) {
    super(other);
  }

  @Override
  public Attribute copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Attribute(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    return name() + ": " + attributeValue().translate(target);
  }

  public String name() {
    return value;
  }

  public Expression<?> attributeValue() {
    return child("value");
  }
}