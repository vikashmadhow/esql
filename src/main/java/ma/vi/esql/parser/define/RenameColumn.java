/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;

import java.util.Map;

public class RenameColumn extends Alteration {
  public RenameColumn(Context context, String from, String to) {
    super(context,
        T2.of("from", new Esql<>(context, from)),
        T2.of("to", new Esql<>(context, to)));
  }

  public RenameColumn(RenameColumn other) {
    super(other);
  }

  @Override
  public RenameColumn copy() {
    if (!copying()) {
      try {
        copying(true);
        return new RenameColumn(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  protected String trans(Target target, Map<String, Object> parameters) {
    return "rename column \"" + from() + "\" to \"" + to() + '"';
  }

  public String from() {
    return childValue("from");
  }

  public String to() {
    return childValue("to");
  }
}
