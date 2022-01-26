package ma.vi.esql.semantic.scope;

import ma.vi.base.lang.NotFoundException;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Scope {

  default String name() {
    return "UnnamedScope";
  }

  /**
   * Find the symbol with the specified name in the current scope only. Returns
   * null if no such symbol exists.
   */
  Symbol find(String symbolName);

  /**
   * Returns the symbol with the specified name if it exists in the current scope
   * only. Throws {@link NotFoundException} if symbol is not found.
   */
  default Symbol get(String symbolName) throws NotFoundException {
    Symbol s = find(symbolName);
    if (s == null) {
      throw new NotFoundException("Symbol " + symbolName + " not found in scope " + name());
    }
    return s;
  }

  int addSymbol(Symbol symbol) throws SymbolAlreadyDefinedException;

  Scope parentScope();

//  /**
//   * Finds the symbol with the specified name if it exists in the current scope
//   * or on of its ancestor scopes. Return null if the symbol is not found.
//   */
//  default Symbol findSymbol(String symbolName) {
//    Symbol s = find(symbolName);
//    if (s == null && parentScope() != null) {
//      s = parentScope().findSymbol(symbolName);
//    }
//    return s;
//  }

//  /**
//   * Finds the symbol with the specified name if it exists in the current scope
//   * or on of its ancestor scopes. Throws {@link NotFoundException} if the symbol
//   * is not found.
//   */
//  default Symbol getSymbol(String symbolName) throws NotFoundException {
//    Symbol s = findSymbol(symbolName);
//    if (s == null) {
//      throw new NotFoundException("Symbol " + symbolName + " not found in scope "
//                                 + name() + " or in any of its ancestors");
//    }
//    return s;
//  }
}