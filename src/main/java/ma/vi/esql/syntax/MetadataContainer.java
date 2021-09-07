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
public abstract class MetadataContainer<V, R> extends Expression<V, R> {
  public MetadataContainer(Context context,
                           V value,
                           T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public MetadataContainer(Context context, V value, List<Esql<?, ?>> children) {
    super(context, value, children);
  }

  public MetadataContainer(MetadataContainer<V, R> other) {
    super(other);
  }

  public MetadataContainer(MetadataContainer<V, R> other, V value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract MetadataContainer<V, R> copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract MetadataContainer<V, R> copy(V value, T2<String, ? extends Esql<?, ?>>... children);

  /**
   * Metadata.
   */
  public Metadata metadata() {
    return child("metadata");
  }

//  /**
//   * Set metadata.
//   */
//  public void metadata(Metadata metadata) {
//    child("metadata", metadata);
//  }

  public Attribute attribute(String name) {
    return metadata() == null ? null : metadata().attribute(name);
  }
//
//  public void attribute(String name, Expression<?, String> value) {
//    if (metadata() == null) {
//      metadata(new Metadata(context, new ArrayList<>()));
//    }
//    metadata().attribute(name, value);
//  }
}
