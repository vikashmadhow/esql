package ma.vi.esql.exec.composable;

import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.query.GroupBy;

/**
 * A column that can be composed into a query.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record ComposableColumn(String       table,
                               String       alias,
                               String       name,
                               String       expression,
                               Metadata     metadata,
                               GroupBy.Type group,
                               Order        order) implements Composable {
  public ComposableColumn(String   table,
                          String   name,
                          String   expression,
                          Metadata metadata) {
    this(table,
         table == null ? null : Type.unqualifiedName(table),
         name,
         expression,
         metadata,
         null,
         Composable.Order.NONE);
  }

  public ComposableColumn(String   table,
                          String   alias,
                          String   name,
                          String   expression,
                          Metadata metadata) {
    this(table,
         alias,
         name,
         expression,
         metadata,
         null,
         Composable.Order.NONE);
  }
}