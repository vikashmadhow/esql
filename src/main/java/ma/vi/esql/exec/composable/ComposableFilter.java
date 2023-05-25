package ma.vi.esql.exec.composable;

import ma.vi.esql.semantic.type.Type;

import java.util.Map;
import java.util.stream.Stream;

/**
 * A filter that can be composed into a Query.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record ComposableFilter(Op      op,
                               String  table,
                               String  alias,
                               String  expression,
                               boolean exclude,
                               ComposableFilter... children) implements Composable {
  /**
   * Returns a new ComposableFilter with its table replaced if it is contained
   * as a key in the replacements map. The replacement is performed recursively
   * on all children of this filter.
   */
  public ComposableFilter replaceTable(Map<String, String> replacements) {
    if (replacements.containsKey(table)) {
      return new ComposableFilter(op,
                                  replacements.get(table),
                                  alias,
                                  expression,
                                  exclude,
                                  children.length == 0
                                  ? children
                                  : Stream.of(children)
                                          .map(c -> c.replaceTable(replacements))
                                          .toArray(ComposableFilter[]::new));
    }
    return this;
  }

  public ComposableFilter(String table, String expression) {
    this(Composable.Op.AND,
         table,
         Type.unqualifiedName(table),
         expression,
         false);
  }

  public ComposableFilter(String table, String alias, String expression) {
    this(Composable.Op.AND,
         table,
         alias,
         expression,
         false);
  }

  public ComposableFilter(Composable.Op op, ComposableFilter... children) {
    this(op, false, children);
  }

  public ComposableFilter(Composable.Op op, boolean exclude, ComposableFilter... children) {
    this(op, null, null, null, exclude, children);
  }
}
