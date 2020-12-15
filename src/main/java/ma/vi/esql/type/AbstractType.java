/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.type;

import ma.vi.esql.parser.expression.Expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static ma.vi.base.lang.Errors.checkArgument;

/**
 * An abstract type implementing type equality by name.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractType implements Type {
  AbstractType(String name) {
    checkArgument(name != null && !name.trim().isEmpty(), "Name cannot be null or blank");
    this.name = name;
  }

  AbstractType(AbstractType other) {
    this.name = other.name;
    if (!other.attributesIsNull()) {
      for (Map.Entry<String, Expression<?>> a: other.attributes().entrySet()) {
        this.attribute(a.getKey(), a.getValue());
      }
    }
  }

  public ConcurrentMap<String, Expression<?>> attributes() {
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
    if (!(o instanceof AbstractType)) return false;
    AbstractType type = (AbstractType)o;
    return name.equals(type.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public void close() {
//    if (!closed() && !closing()) {
//      try {
//        closing(true);
//        if (attributes != null) {
//          for (DbExpression expr: attributes.values()) {
//            expr.close();
//          }
//          attributes.clear();
//        }
//      } finally {
//        closing(false);
//        closed(true);
//      }
//    }
  }

  @Override
  public boolean closed() {
    return closed;
  }

  @Override
  public void closed(boolean closed) {
    this.closed = closed;
  }

  @Override
  public boolean closing() {
    return closing;
  }

  @Override
  public void closing(boolean closing) {
    this.closing = closing;
  }

  @Override
  public boolean copying() {
    return copying;
  }

  @Override
  public void copying(boolean copying) {
    this.copying = copying;
  }

  /**
   * Set to true while copying.
   */
  private volatile boolean copying;

  /**
   * Set to true while closing.
   */
  private volatile boolean closing;

  /*
   * True when the object is closed.
   */
  private volatile boolean closed;

  /**
   * The unique qualified type name.
   */
  protected String name;

  private ConcurrentMap<String, Expression<?>> attributes;
}