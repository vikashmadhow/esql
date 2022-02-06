package ma.vi.esql.exec.env;

import ma.vi.base.lang.NotFoundException;
import ma.vi.esql.semantic.scope.SymbolAlreadyDefinedException;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Environment {
  default String name() {
    return "Environment";
  }

  boolean has(String symbol);

  default boolean knows(String symbol) {
    return has(symbol)
        || (parent() != null && parent().knows(symbol));
  }

  void add(String symbol, Object value) throws SymbolAlreadyDefinedException;

  <R> R get(String symbol) throws NotFoundException;

  void set(String symbol, Object value) throws NotFoundException;

  Environment parent();
}
