/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.base.collections.Maps;
import ma.vi.esql.syntax.query.Column;

import java.util.Arrays;
import java.util.Map;

/**
 * The value for a column along with the values of its metadata attributes read
 * from the resultset of a query.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record ResultColumn<T>(T value,
                              Column column,
                              Map<String, Object> metadata) {
  @Override
  public String toString() {
    return column.name() + '='
         + (value == null ? "null" : value.getClass().isArray() ? Arrays.toString((Object[])value) : value)
         + (metadata != null ? ' ' + Maps.toString(metadata) : "");
  }
}
