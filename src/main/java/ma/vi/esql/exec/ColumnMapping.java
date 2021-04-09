/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.base.tuple.T3;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.type.Type;

import java.util.List;
import java.util.Map;

/**
 * Maps a query column (in a select, e.g.) to the column index in the resultset,
 * its type, and the indices of its metadata attributes in the resultset. Any
 * attributes which be pre-calculated is also added to the column mapping.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ColumnMapping {
  public ColumnMapping(int valueIndex,
                       Column column,
                       Type valueType,
                       List<T3<Integer, String, Type>> attributeIndices,
                       Map<String, Object> attributes) {
    this.valueIndex = valueIndex;
    this.column = column;
    this.valueType = valueType;
    this.attributeIndices = attributeIndices;
    this.attributes = attributes;
  }

  /**
   * The index in the resultset for the value of this column.
   */
  public final int valueIndex;

  public final Column column;

  /**
   * The type of the value.
   */
  public final Type valueType;

  /**
   * Maps the index in the resultset of an attribute value to its
   * corresponding attribute name and type.
   */
  public final List<T3<Integer, String, Type>> attributeIndices;

  /**
   * Pre-computed attributes for the column; used primarily for attributes with literal values.
   */
  public final Map<String, Object> attributes;
}
