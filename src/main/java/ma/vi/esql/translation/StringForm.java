package ma.vi.esql.translation;

/**
 * Implemented to produce a string representation of an object.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface StringForm {
  /**
   * Called to construct the string form of the object.
   * @param st The string builder into which the string form is to be appended.
   * @param level A level indicator when building string form of hierarchical
   *              objects.
   * @param indent The number of spaces to use to indent components of the string
   *               form of the object.
   */
  void _toString(StringBuilder st,
                 int           level,
                 int           indent);
}
