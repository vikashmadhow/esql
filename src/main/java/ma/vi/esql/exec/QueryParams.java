package ma.vi.esql.exec;

import ma.vi.base.reflect.Dissector;
import ma.vi.base.reflect.Property;

import java.util.*;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class QueryParams {
  public QueryParams add(Param p) {
    params.put(p.a, p);
    return this;
  }

  public QueryParams add(String name, Object value) {
    params.put(name, Param.of(name, value));
    return this;
  }

  public QueryParams add(Object objectAsParams) {
    for (Param p: asParams(objectAsParams)) add(p);
    return this;
  }

  /**
   * Return a parameter value by name, if found in the parameter list, or empty
   * if not.
   * @param name The parameter name.
   * @return The parameter value if found in the parameter list, or empty otherwise.
   */
  public <T> Optional<T> find(String name) {
    return params.containsKey(name)
         ? Optional.of((T)params.get(name).b)
         : Optional.empty();
  }

  public QueryParams filter(Filter filter) {
    if (filter != null) filters.add(filter);
    return this;
  }

  public QueryParams filters(Collection<Filter> filters) {
    if (filters != null) this.filters.addAll(filters);
    return this;
  }

  public QueryParams and(String table, String alias, String condition, boolean exclude) {
    return filter(new Filter(Filter.Op.AND, table, alias, condition, exclude));
  }

  public QueryParams and(String table, String alias, String condition) {
    return and(table, alias, condition, false);
  }

  public QueryParams or(String table, String alias, String condition, boolean exclude) {
    return filter(new Filter(Filter.Op.OR, table, alias, condition, exclude));
  }

  public QueryParams or(String table, String alias, String condition) {
    return or(table, alias, condition, false);
  }

  private static Param[] asParams(Object objectAsParams) {
    Map<String, Property> props = Dissector.properties(objectAsParams.getClass());
    Param[] params = new Param[props.size()];
    int i = 0;
    for (Map.Entry<String, Property> prop: props.entrySet()) {
      params[i] = Param.of(prop.getKey(), prop.getValue().get(objectAsParams));
      i++;
    }
    return params;
  }

  protected final Map<String, Param> params = new LinkedHashMap<>();

  protected final List<Filter> filters = new ArrayList<>();
}
