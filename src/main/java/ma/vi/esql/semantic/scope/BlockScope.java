package ma.vi.esql.semantic.scope;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class BlockScope extends AbstractScope {
  public BlockScope(Scope parentScope) {
    super(parentScope);
  }

  @Override
  public void addSymbol(Symbol symbol) throws SymbolAlreadyDefinedException {
    /*
     * Go up until we reach a functional scope. Symbols defined outside of the
     * containing function can safely be shadowed in the current block.
     */
    Scope scope = this;
    while (scope != null) {
      if (scope.findSymbol(symbol.name()) != null) {
        throw new SymbolAlreadyDefinedException(symbol.name() + " is already defined");
      }
      if (scope instanceof FunctionScope) {
        break;
      }
      scope = scope.parentScope();
    }
    symbols.put(symbol.name(), symbol);
  }
}
