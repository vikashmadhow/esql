package ma.vi.esql.exec.composable;

import ma.vi.esql.semantic.type.Type;

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
