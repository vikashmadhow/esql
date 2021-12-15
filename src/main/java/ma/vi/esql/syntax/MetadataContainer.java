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
public abstract class MetadataContainer<R> extends Expression<String, R> {
  public MetadataContainer(Context context, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public MetadataContainer(Context context, List<Esql<?, ?>> children) {
    super(context, "MetadataContainer", children);
  }

  public MetadataContainer(MetadataContainer<R> other) {
    super(other);
  }

  public MetadataContainer(MetadataContainer<R> other,
                           String value,
                           T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract MetadataContainer<R> copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract MetadataContainer<R> copy(String value, T2<String, ? extends Esql<?, ?>>... children);

  /**
   * Metadata.
   */
  public Metadata metadata() {
    return child("metadata");
  }

  /**
   * Set metadata.
   */
  public <T extends MetadataContainer<?>> T metadata(Metadata metadata) {
    return (T)set("metadata", metadata);
  }

  public Attribute attribute(String name) {
    return metadata() == null ? null : metadata().attribute(name);
  }
}
