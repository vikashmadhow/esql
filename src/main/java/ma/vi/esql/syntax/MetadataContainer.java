/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.expression.Expression;

import java.util.List;

/**
 * A part of an Esql statement to which metadata can be associated.
 * E.g. columns and select statements.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class MetadataContainer<T> extends Expression<String, T> {
  @SafeVarargs
  public MetadataContainer(Context context, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public MetadataContainer(Context context, List<Esql<?, ?>> children) {
    super(context, "MetadataContainer", children);
  }

  public MetadataContainer(MetadataContainer<T> other) {
    super(other);
  }

  @SafeVarargs
  public MetadataContainer(MetadataContainer<T> other,
                           String value,
                           T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract MetadataContainer<T> copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract MetadataContainer<T> copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  /**
   * Metadata.
   */
  public Metadata metadata() {
    return child("metadata");
  }

  /**
   * Set metadata.
   */
  public <M extends MetadataContainer<?>> M metadata(Metadata metadata) {
    return (M)set("metadata", metadata);
  }

  public Attribute attribute(String name) {
    return metadata() == null ? null : metadata().attribute(name);
  }
}
