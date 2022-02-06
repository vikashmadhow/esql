package ma.vi.esql.exec.env;

import ma.vi.base.lang.NotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractEnvironment implements Environment {
  public AbstractEnvironment() {
    this(null);
  }

  public AbstractEnvironment(Environment parent) {
    this(null, parent);
  }

  public AbstractEnvironment(String name, Environment parent) {
    this.name = name != null ? name : "Environment";
    this.parent = parent;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public boolean has(String symbol) {
    return values.containsKey(symbol);
  }

  @Override
  public <R> R get(String symbol) throws NotFoundException {
    if (values.containsKey(symbol)) {
      return (R)values.get(symbol);
    } else if (parent != null) {
      return parent.get(symbol);
    } else {
      throw new NotFoundException(symbol + " not found in current environment or any of its ancestor environments");
    }
  }

  @Override
  public void set(String symbol, Object value) throws NotFoundException {
    if (values.containsKey(symbol)) {
      values.put(symbol, value);
    } else if (parent != null) {
      parent.set(symbol, value);
    } else {
      throw new NotFoundException(symbol + " has not been defined in environment");
    }
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public Environment parent() {
    return parent;
  }

  protected final Map<String, Object> values = new HashMap<>();

  protected final Environment parent;

  public final String name;
}
