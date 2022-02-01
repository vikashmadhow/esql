package ma.vi.esql.semantic.scope;

import ma.vi.base.lang.NotFoundException;
import ma.vi.esql.syntax.EsqlPath;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Scope {
  /**
   * Find the symbol with the specified name in the current scope only. Returns
   * null if no such symbol exists.
   */
  Symbol findSymbol(String symbolName);

  /**
   * Returns the symbol with the specified name if it exists in the current scope
   * only. Throws {@link NotFoundException} if symbol is not found.
   */
  default Symbol getSymbol(String symbolName) throws NotFoundException {
    Symbol s = findSymbol(symbolName);
    if (s == null) {
      throw new NotFoundException("Symbol " + symbolName + " not found in scope");
    }
    return s;
  }

  void addSymbol(Symbol symbol) throws SymbolAlreadyDefinedException;

  Scope parentScope();

  static Symbol findSymbol(String symbolName, EsqlPath path) {
    EsqlPath p = path;
    while (p != null) {
      Scope scope = p.head().scope();
      if (scope != null) {
        Symbol symbol = scope.findSymbol(symbolName);
        if (symbol != null) {
          return symbol;
        }
      }
      p = p.tail();
    }
    return null;
  }
}