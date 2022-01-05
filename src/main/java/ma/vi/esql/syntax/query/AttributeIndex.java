package ma.vi.esql.syntax.query;

import ma.vi.esql.semantic.type.Type;

/**
 * Holds information on the column containing the value of an attribute in the
 * resultset produced by a query.
 *
 * @param index The index of the column in the resultset containing the value of
 *              the attribute.
 * @param name The name of the attribute.
 * @param type The expected type of the value of the attribute.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record AttributeIndex(int index, String name, Type type) {
  @Override
  public String toString() {
    return name + ":" + type + ", idx " + index;
  }
}
