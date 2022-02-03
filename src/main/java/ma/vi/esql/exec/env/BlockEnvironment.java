package ma.vi.esql.exec.env;

import ma.vi.esql.semantic.scope.SymbolAlreadyDefinedException;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class BlockEnvironment extends AbstractEnvironment {
  public BlockEnvironment() {
    this(null);
  }

  public BlockEnvironment(AbstractEnvironment parent) {
    super(parent);
  }

  @Override
  public void add(String symbol, Object value) throws SymbolAlreadyDefinedException {
    /*
     * Go up until we reach a functional environment. Symbols defined outside of
     * the containing function can safely be shadowed in the current block.
     */
    Environment env = this;
    while (env != null) {
      if (env.has(symbol)) {
        throw new SymbolAlreadyDefinedException(symbol + " is already defined");
      }
      if (env instanceof FunctionEnvironment) {
        break;
      }
      env = env.parent();
    }
    values.put(symbol, value);
  }
}