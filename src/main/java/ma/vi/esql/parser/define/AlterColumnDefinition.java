/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.type.Type;

import static ma.vi.esql.parser.Translatable.Target.ESQL;

public class AlterColumnDefinition extends Define<String> {
  public AlterColumnDefinition(Context context,
                               String toName, Type toType,
                               boolean setNotNull, boolean dropNotNull,
                               Expression<?> setDefault, boolean dropDefault,
                               Metadata metadata) {
    super(context, toName,
        T2.of("toType", new Esql<>(context, toType)),
        T2.of("setNotNull", new Esql<>(context, setNotNull)),
        T2.of("dropNotNull", new Esql<>(context, dropNotNull)),
        T2.of("setDefault", setDefault),
        T2.of("dropDefault", new Esql<>(context, dropDefault)),
        T2.of("metadata", metadata));
  }

  public AlterColumnDefinition(AlterColumnDefinition other) {
    super(other);
  }

  @Override
  public AlterColumnDefinition copy() {
    if (!copying()) {
      try {
        copying(true);
        return new AlterColumnDefinition(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public String translate(Target target) {
    return (toName() == null ? "" : toName() + ' ') +
        (toType() == null ? "" : type().translate(target) + ' ') +
        (setNotNull() ? "null " : "") +
        (dropNotNull() ? "not null " : "") +
        (setDefault() != null ? "default " + setDefault().translate(target) + ' ' : "") +
        (dropDefault() ? "no default " : "") +
        (target == ESQL ? (metadata() != null ? metadata().translate(target) : "") : "");
  }

  public String toName() {
    return childValue("toName");
  }

  public Type toType() {
    return childValue("toType");
  }

  public Boolean setNotNull() {
    return childValue("setNotNull");
  }

  public Boolean dropNotNull() {
    return childValue("dropNotNull");
  }

  public Expression<?> setDefault() {
    return child("setDefault");
  }

  public Boolean dropDefault() {
    return childValue("dropDefault");
  }

  public Metadata metadata() {
    return child("metadata");
  }
}