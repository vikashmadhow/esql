package ma.vi.esql.exec.env;

import ma.vi.esql.semantic.scope.SymbolAlreadyDefinedException;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class FunctionEnvironment extends AbstractEnvironment {
  public FunctionEnvironment() {
    this(null);
  }

  public FunctionEnvironment(Environment parent) {
    super(parent);
  }

  @Override
  public void add(String symbol, Object value) throws SymbolAlreadyDefinedException {
    if (values.containsKey(symbol)) {
      throw new SymbolAlreadyDefinedException(symbol + " is already defined");
    }
    values.put(symbol, value);
  }
}