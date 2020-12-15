package ma.vi.esql.exec;

import ma.vi.esql.type.Type;
import ma.vi.base.tuple.T3;

import java.util.List;
import java.util.Map;

/**
 * Maps a projected column (in a select, e.g.) to its type (regular field or field metadata).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Mapping {
  public Mapping(int valueIndex,
                 Type valueType,
                 List<T3<Integer, String, Type>> attributeIndices,
                 Map<String, Object> attributes) {
    this.valueIndex = valueIndex;
    this.valueType = valueType;
    this.attributeIndices = attributeIndices;
    this.attributes = attributes;
  }

  /**
   * The index in the resultset for the value of this column.
   */
  public final int valueIndex;

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
