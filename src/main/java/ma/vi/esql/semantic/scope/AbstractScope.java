package ma.vi.esql.semantic.scope;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractScope implements Scope {
  public AbstractScope(String name,
                       Scope parentScope,
                       Allocator allocator) {
    this.name = name;
    this.parentScope = parentScope;
    this.allocator = allocator;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Symbol find(String symbolName) {
    return symbols.get(symbolName);
  }

  @Override
  public Scope parentScope() {
    return parentScope;
  }

  protected final String name;
  protected final Scope parentScope;
  protected final Map<String, Symbol> symbols = new HashMap<>();
  public final Allocator allocator;
}
