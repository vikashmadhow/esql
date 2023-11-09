package ma.vi.esql.exec.composable;

import ma.vi.esql.semantic.type.Type;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A filter that can be composed into a Query.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ComposableFilter implements Composable {
  public enum Op { AND, OR }

  /**
   *
   */
  public ComposableFilter(String  table,
                          String  alias,
                          String  expression,
                          boolean exclude) {
    this.table = table;
    this.alias = alias;
    this.expression = expression;
    this.exclude = exclude;
  }

  public ComposableFilter(String table, String expression) {
    this(table,
         Type.unqualifiedName(table),
         expression,
         false);
  }

  public ComposableFilter(String table, String alias, String expression) {
    this(table,
         alias,
         expression,
         false);
  }

  public ComposableFilter() {
    this(false);
  }

  public ComposableFilter(boolean exclude) {
    this(null, null, null, exclude);
  }

  public static ComposableFilter from(String json) {
    return from(new JSONObject(json));
  }

  public static ComposableFilter from(JSONObject json) {
    if (json.has("filter")) {
      JSONArray filter = json.getJSONArray("filter");
      List<ComposableFilter> filters = new ArrayList<>();
      for (int i = 0; i < filter.length(); i++) {
        filters.add(ComposableFilter.from(filter.getJSONObject(i)));
      }
      return new CombinedComposableFilter(
        Op.valueOf(json.optString("op", "and").trim().toUpperCase()),
        json.optBoolean("exclude", false),
        filters);
    } else {
      return new ComposableFilter(json.getString("table"),
                                  json.getString("alias"),
                                  json.optString("where", json.optString("condition", json.optString("expression"))),
                                  json.optBoolean("exclude", false));
    }
  }

  /**
   * Returns a new ComposableFilter with its table replaced if it is contained
   * as a key in the replacements map. The replacement is performed recursively
   * on all children of this filter.
   */
  public ComposableFilter replaceTable(Map<String, String> replacements) {
    if (replacements.containsKey(table)) {
      return new ComposableFilter(replacements.get(table),
                                  alias,
                                  expression,
                                  exclude);
//                                  children.length == 0
//                                  ? children
//                                  : Stream.of(children)
//                                          .map(c -> c.replaceTable(replacements))
//                                          .toArray(ComposableFilter[]::new));
    }
    return this;
  }

  @Override
  public String table() {
    return table;
  }

  @Override
  public String alias() {
    return alias;
  }

  @Override
  public String expression() {
    return expression;
  }

  public boolean exclude() {
    return exclude;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (ComposableFilter)obj;
    return Objects.equals(this.table, that.table) &&
           Objects.equals(this.alias, that.alias) &&
           Objects.equals(this.expression, that.expression) &&
           this.exclude == that.exclude;
  }

  @Override
  public int hashCode() {
    return Objects.hash(table, alias, expression, exclude);
  }

  @Override
  public String toString() {
    return "ComposableFilter[" +
            "table=" + table + ", " +
            "alias=" + alias + ", " +
            "expression=" + expression + ", " +
            "exclude=" + exclude + ']';
  }

  private final String  table;
  private final String  alias;
  private final String  expression;
  private final boolean exclude;
}
