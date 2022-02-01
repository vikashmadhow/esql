package ma.vi.esql.semantic.scope;

import ma.vi.esql.semantic.type.Type;

import static ma.vi.esql.semantic.type.Types.UnknownType;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Symbol {
  String name();
  default Type type() {
    return UnknownType;
  }

  static Symbol of(String name) {
    return of(name, UnknownType);
  }

  static Symbol of(String name, Type type) {
    return new Symbol() {
      @Override public String name() { return name; }
      @Override public Type   type() { return type; }
    };
  }
}