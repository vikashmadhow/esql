package ma.vi.esql.semantic.scope;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class FunctionScope extends AbstractScope {
  public FunctionScope(Scope parentScope) {
    super(parentScope);
  }

  @Override
  public void addSymbol(Symbol symbol) throws SymbolAlreadyDefinedException {
    if (symbols.containsKey(symbol.name())) {
      throw new SymbolAlreadyDefinedException(symbol.name() + " is already defined");
    }
    symbols.put(symbol.name(), symbol);
  }
}