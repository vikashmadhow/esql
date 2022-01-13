package ma.vi.esql.semantic.scope;

import ma.vi.base.lang.NotFoundException;

import java.util.List;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Scope {

  default String name() {
    return "UnnamedScope";
  }

  Symbol find(String symbolName);

  default Symbol get(String symbolName) throws NotFoundException {
    Symbol s = find(symbolName);
    if (s == null) {
      throw new NotFoundException("Symbol " + symbolName + " not found in scope " + name());
    }
    return s;
  }

  default Symbol findSymbol(String symbolName) {
    Symbol s = find(symbolName);
    if (s == null && parentScope() != null) {
      s = parentScope().findSymbol(symbolName);
    }
    return s;
  }

  default Symbol getSymbol(String symbolName) throws NotFoundException {
    Symbol s = findSymbol(symbolName);
    if (s == null) {
      throw new NotFoundException("Symbol " + symbolName + " not found in scope "
                                 + name() + " or in any of its ancestors");
    }
    return s;
  }

  void addSymbol(Symbol symbol);

  Scope addScope(Scope scope);

  void removeScope(Scope scope);

  Scope scope(String name);

  List<Scope> scopes();

  Scope parentScope();
}

interface RelationScope extends Scope {
  @Override
  default Symbol findSymbol(String symbolName) {
    return find(symbolName);
  }
}