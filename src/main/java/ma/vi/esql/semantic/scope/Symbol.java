package ma.vi.esql.semantic.scope;

import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Symbol {
  String name();
  default Type type() {
    return Types.UnknownType;
  }
}