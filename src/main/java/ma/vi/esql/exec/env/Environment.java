package ma.vi.esql.exec.env;

import ma.vi.base.lang.NotFoundException;
import ma.vi.esql.semantic.scope.SymbolAlreadyDefinedException;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Environment {
  boolean has(String symbol);

  void add(String symbol, Object value) throws SymbolAlreadyDefinedException;

  Object get(String symbol) throws NotFoundException;

  void set(String symbol, Object value) throws NotFoundException;
}
