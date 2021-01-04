/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.type;

import ma.vi.base.string.Strings;
import ma.vi.base.trie.PathTrie;
import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.parser.query.TableExpr;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * A selection of columns from a relation (also known
 * as a projection in SQL and relational calculus).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Selection extends Relation {

  public Selection(List<Column> columns, TableExpr from) {
    super((from == null ? "" : from.toString() + '.') + "select_" + Strings.random());
    this.columns = columns;
    this.from = from;
    for (Column c: columns) {
      this.columnsByAlias.put(c.alias(), c);
    }
  }

  public Selection(Selection other) {
    super(other.name);
    this.columns = new ArrayList<>();
    for (Column column: other.columns) {
      Column col = column.copy();
      this.columns.add(col);
    }
    this.from = other.from == null ? null : other.from.copy();
  }

  @Override
  public Selection copy() {
    if (!copying()) {
      try {
        copying(true);
        return new Selection(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public List<Column> columns() {
    return columns;
  }

  @Override
  public List<Column> columns(String relationAlias, String prefix) {
    List<T2<String, Column>> cols = columnsByAlias.getPrefixed(prefix);
    return cols.stream()
               .map(t -> t.b().copy())
               .collect(toList());
  }

  public TableExpr from() {
    return from;
  }

  /**
   * Relation columns.
   */
  private final List<Column> columns;

  private final PathTrie<Column> columnsByAlias = new PathTrie<>();

  private final TableExpr from;
}
