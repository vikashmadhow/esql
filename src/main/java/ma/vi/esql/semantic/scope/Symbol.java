package ma.vi.esql.semantic.scope;

import ma.vi.esql.semantic.type.Type;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Symbol {
  String name();
  default Type type() {
    return Type.Void;
  }
}
