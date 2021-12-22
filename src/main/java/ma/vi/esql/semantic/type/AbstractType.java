/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.esql.syntax.define.Attribute;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static ma.vi.base.lang.Errors.checkArgument;

/**
 * An abstract type implementing type equality by name.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractType implements Type {
  public AbstractType(String name) {
    checkArgument(name != null && !name.trim().isEmpty(), "Name cannot be null or blank");
    this.name = name;
  }

  AbstractType(AbstractType other) {
    this.name = other.name;
    if (!other.attributesIsNull()) {
      this.attributes = new ConcurrentHashMap<>(other.attributes());
    }
  }

  @Override
  public ConcurrentMap<String, Attribute> attributes() {
    if (this.attributes == null) {
      this.attributes = new ConcurrentHashMap<>();
    }
    return attributes;
  }

  public boolean attributesIsNull() {
    return attributes == null;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public void name(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AbstractType type)) return false;
    return name.equals(type.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /**
   * The unique qualified type name.
   */
  protected String name;

  private ConcurrentMap<String, Attribute> attributes;
}