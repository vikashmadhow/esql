/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.Expression;

import java.util.Map;

import static ma.vi.esql.syntax.Translatable.Target.ESQL;

/**
 * An alter column definition (change name, type, etc.).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AlterColumnDefinition extends Define<String> {
  public AlterColumnDefinition(Context context,
                               String toName,
                               Type toType,
                               boolean setNotNull,
                               boolean dropNotNull,
                               Expression<?, String> setDefault,
                               boolean dropDefault,
                               Metadata metadata) {
    super(context,
          toName,
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

  public AlterColumnDefinition(AlterColumnDefinition other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public AlterColumnDefinition copy() {
    return new AlterColumnDefinition(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public AlterColumnDefinition copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new AlterColumnDefinition(this, value, children);
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    return (toName() == null ? "" : toName() + ' ')
         + (toType() == null ? "" : type(path).translate(target, path, parameters) + ' ')
         + (setNotNull() ? "null " : "")
         + (dropNotNull() ? "not null " : "")
         + (setDefault() != null ? "default " + setDefault().translate(target, path.add(setDefault()), parameters) + ' ' : "")
         + (dropDefault() ? "no default " : "")
         + (target == ESQL ? (metadata() != null ? metadata().translate(target, path.add(metadata()), parameters) : "") : "");
  }

  public String toName() {
    return value;
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

  public Expression<?, String> setDefault() {
    return child("setDefault");
  }

  public Boolean dropDefault() {
    return childValue("dropDefault");
  }

  public Metadata metadata() {
    return child("metadata");
  }
}