package ma.vi.esql.exec;

import ma.vi.base.reflect.Dissector;
import ma.vi.base.reflect.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class QueryParams {
  public QueryParams add(Param p) {
    params.add(p);
    return this;
  }

  public QueryParams add(String name, Object value) {
    params.add(Param.of(name, value));
    return this;
  }

  public QueryParams add(Object objectAsParams) {
    params.addAll(Arrays.asList(asParams(objectAsParams)));
    return this;
  }

  public QueryParams filter(Filter filter) {
    filters.add(filter);
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

  protected final List<Param> params = new ArrayList<>();
  protected final List<Filter> filters = new ArrayList<>();
}
