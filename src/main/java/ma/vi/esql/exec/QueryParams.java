package ma.vi.esql.exec;

import ma.vi.base.lang.NotFoundException;
import ma.vi.base.reflect.Dissector;
import ma.vi.base.reflect.Property;
import ma.vi.esql.exec.composable.CombinedComposableFilter;
import ma.vi.esql.exec.composable.ComposableColumn;
import ma.vi.esql.exec.composable.ComposableFilter;
import ma.vi.esql.exec.env.FunctionEnvironment;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.query.GroupBy;

import java.util.*;

import static ma.vi.esql.database.EsqlConnection.NULL_CONNECTION;
import static ma.vi.esql.exec.composable.ComposableFilter.Op.AND;
import static ma.vi.esql.exec.composable.ComposableFilter.Op.OR;
import static ma.vi.esql.translation.Translatable.Target.ESQL;

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
   * Return a parameter value by name, if found in the parameter list, or throws
   * `NotFoundException` if not.
   * @param name The parameter name.
   * @return The parameter value if found in the parameter list, or empty otherwise.
   */
  public <T> T get(String name) {
    if (params.containsKey(name)) {
      return (T)params.get(name).b;
    } else {
      throw new NotFoundException("Could find parameter " + name);
    }
  }

  /**
   * Return a parameter value by name, if found in the parameter list, or empty
   * if not.
   * @param name The parameter name.
   * @return The parameter value if found in the parameter list, or empty otherwise.
   */
  public <T> Optional<T> find(String name) {
    return params.containsKey(name) && params.get(name).b != null
         ? Optional.of((T)params.get(name).b)
         : Optional.empty();
  }

  /**
   * Returns a map view of the parameters with the parameter values in their
   * raw form (not as ESQL expressions). This map view provides simpler access
   * to client code that need to work with the parameter values.
   */
  public Map<String, Object> toMap() {
    Map<String, Object> map = new LinkedHashMap<>();
    EsqlPath path = new EsqlPath(null);
    for (Map.Entry<String, Param> e: params.entrySet()) {
      Object p = e.getValue().b();
      map.put(e.getKey(),
              p instanceof Esql<?, ?> expr
              ? expr.exec(ESQL,
                          NULL_CONNECTION,
                          path.add(expr),
                          null,
                          new FunctionEnvironment())
              : p);
    }
    return map;
  }

  public QueryParams filter(ComposableFilter filter) {
//    if (filter != null) filters.add(filter);
    if (filter != null) {
      if (filters == null) {
        filters = filter;
      } else if (filters instanceof CombinedComposableFilter c) {
        filters = c.add(filter);
      } else {
        filters = new CombinedComposableFilter(AND, filter, filters);
      }
    }
    return this;
  }

//  public QueryParams restrict(ComposableFilter filter) {
//    if (filter != null) {
//      if (filters == null) {
//        filters = filter;
//      } else {
//        filters = new CombinedComposableFilter(AND, filter, filters);
//      }
//    }
//    return this;
//  }

  public QueryParams and(ComposableFilter filter) {
//    if (filter != null) filters.add(filter);
    if (filter != null) {
      if (filters == null) {
        filters = filter;
      } else if (filters instanceof CombinedComposableFilter c && c.op() == AND) {
        filters = c.add(filter);
      } else {
        filters = new CombinedComposableFilter(AND, filter, filters);
      }
    }
    return this;
  }

  public QueryParams or(ComposableFilter filter) {
//    if (filter != null) filters.add(filter);
    if (filter != null) {
      if (filters == null) {
        filters = filter;
      } else if (filters instanceof CombinedComposableFilter c && c.op() == OR) {
        filters = c.add(filter);
      } else {
        filters = new CombinedComposableFilter(OR, filters, filter);
      }
    }
    return this;
  }

  public QueryParams filters(Collection<ComposableFilter> filters) {
    filters.forEach(this::filter);
    return this;
  }

  public QueryParams and(String table, String alias, String condition, boolean exclude) {
    return and(new ComposableFilter(table, alias, condition, exclude));
//    return filter(new ComposableFilter(ComposableFilter.Op.AND, table, alias, condition, exclude));
  }

  public QueryParams and(String table, String alias, String condition) {
    return and(table, alias, condition, false);
  }

  public QueryParams or(String table, String alias, String condition, boolean exclude) {
    return or(new ComposableFilter(table, alias, condition, exclude));
//    return filter(new ComposableFilter(ComposableFilter.Op.OR, table, alias, condition, exclude));
  }

  public QueryParams or(String table, String alias, String condition) {
    return or(table, alias, condition, false);
  }

  public QueryParams column(ComposableColumn column) {
    if (column != null) columns.add(column);
    return this;
  }

  public QueryParams column(String table, String expression) {
    columns.add(new ComposableColumn(table, null, null, expression, null));
    return this;
  }

  public QueryParams column(String table, String alias, String expression) {
    columns.add(new ComposableColumn(table, alias, null, expression, null));
    return this;
  }

  public QueryParams column(String table, String alias, String name, String expression) {
    columns.add(new ComposableColumn(table, alias, name, expression, null));
    return this;
  }

  public QueryParams column(String table, String alias, String name, String expression, GroupBy.Type group) {
    columns.add(new ComposableColumn(table, alias, name, expression, null, group, ComposableColumn.Order.NONE));
    return this;
  }

  public QueryParams column(String table, String alias, String name, String expression, Metadata metadata) {
    columns.add(new ComposableColumn(table, alias, name, expression, metadata, null, ComposableColumn.Order.NONE));
    return this;
  }

  public QueryParams column(String table, String alias, String name, String expression, Metadata metadata, GroupBy.Type group) {
    columns.add(new ComposableColumn(table, alias, name, expression, metadata, group, ComposableColumn.Order.NONE));
    return this;
  }

  public QueryParams column(String table, String alias, String name, String expression, ComposableColumn.Order order) {
    columns.add(new ComposableColumn(table, alias, name, expression, null, null, order));
    return this;
  }

  public QueryParams column(String table, String alias, String name, String expression, Metadata metadata, ComposableColumn.Order order) {
    columns.add(new ComposableColumn(table, alias, name, expression, metadata, null, order));
    return this;
  }

  public QueryParams column(String table, String alias, String name, String expression, Metadata metadata, GroupBy.Type group, ComposableColumn.Order order) {
    columns.add(new ComposableColumn(table, alias, name, expression, metadata, group, order));
    return this;
  }

  public QueryParams columns(Collection<ComposableColumn> columns) {
    if (columns != null) this.columns.addAll(columns);
    return this;
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

  public QueryParams params(Collection<Param> params) {
    for (Param p: params) add(p);
    return this;
  }

  public QueryParams clear() {
    params.clear();
    filters = null;
    columns.clear();
    return this;
  }

  public QueryParams clearParams() {
    params.clear();
    return this;
  }

  public QueryParams clearFilters() {
//    filters.clear();
    filters = null;
    return this;
  }

  public QueryParams clearColumns() {
    columns.clear();
    return this;
  }

  public Map<String, Param> params() {
    return Collections.unmodifiableMap(params);
  }

  public ComposableFilter filters() {
    return filters;
  }

  public List<ComposableColumn> columns() {
    return Collections.unmodifiableList(columns);
  }

  protected final Map<String, Param> params = new LinkedHashMap<>();

//  protected final List<ComposableFilter> filters = new ArrayList<>();
  protected ComposableFilter filters = null;

  protected final List<ComposableColumn> columns = new ArrayList<>();
}
