/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.base.collections.Maps;
import ma.vi.esql.syntax.query.Column;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ResultColumn<T> {
  public ResultColumn(T value, Column column, Map<String, Object> metadata) {
    this.value = value;
    this.column = column;
    this.metadata = metadata;
  }

  @Override
  public String toString() {
    return column.alias() + '='
        + (value == null ? "null" : value.getClass().isArray() ? Arrays.toString((Object[])value) : value)
        + (metadata != null ? ' ' + Maps.toString(metadata) : "");
  }

  /**
   * The value of the field.
   */
  public T value;

  /**
   * Information on the field.
   */
  public final Column column;

  /**
   * Additional metadata attached to the field.
   */
  public final Map<String, Object> metadata;
}
