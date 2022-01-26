package ma.vi.esql.semantic.scope;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class FunctionScope extends AbstractScope {
  public FunctionScope(String name, Scope parentScope, Allocator allocator) {
    super(name, parentScope, allocator);
  }

  @Override
  public int addSymbol(Symbol symbol) throws SymbolAlreadyDefinedException {
    if (symbols.containsKey(symbol.name())) {
      throw new SymbolAlreadyDefinedException(symbol.name() + " is already defined in " + name);
    }
    symbols.put(symbol.name(), symbol);
    return allocator.allocate(symbol);
  }
}