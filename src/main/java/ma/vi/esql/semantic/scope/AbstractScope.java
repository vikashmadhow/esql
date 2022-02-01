package ma.vi.esql.semantic.scope;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractScope implements Scope {
  public AbstractScope(Scope parentScope) {
    this.parentScope = parentScope;
  }

  @Override
  public Symbol findSymbol(String symbolName) {
    return symbols.get(symbolName);
  }

  @Override
  public Scope parentScope() {
    return parentScope;
  }

  protected final Scope parentScope;
  protected final Map<String, Symbol> symbols = new HashMap<>();
}
