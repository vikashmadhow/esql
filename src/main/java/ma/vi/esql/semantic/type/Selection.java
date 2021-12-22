/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.base.string.Strings;
import ma.vi.base.trie.PathTrie;
import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.query.Column;
import ma.vi.esql.syntax.query.TableExpr;

import java.util.*;

/**
 * A selection of columns from a relation (also known
 * as a projection in SQL and relational calculus).
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Selection extends Relation {
  public Selection(List<Column> columns,
                   Collection<Attribute> attributes,
                   TableExpr from) {
    super((from == null ? "" : from.toString() + '.') + "select_" + Strings.random());
    this.columns = columns;
    this.from = from;
    for (Column c: columns) {
      this.columnsByAlias.put(c.name(), c);
    }
    if (attributes != null) {
      attributes(attributes);
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
    return new Selection(this);
  }

  @Override
  public String alias() {
    return null;
  }

  @Override
  public Set<String> aliases() {
    return Collections.emptySet();
  }

  @Override
  public List<T2<Relation, Column>> columns() {
    return columns.stream().map(c -> T2.<Relation, Column>of(this, c)).toList();
  }

  @Override
  public List<T2<Relation, Column>> columns(String prefix) {
    List<T2<String, Column>> cols = columnsByAlias.getPrefixed(prefix);
    Context context = from().context;
    return cols.stream()
               .map(t -> new T2<Relation, Column>(this,
                                                  new Column(context, t.a(),
                                                  new ColumnRef(context, null, t.a()),
                                                 null)))
               .toList();
  }

  @Override
  public T2<Relation, Column> findColumn(ColumnRef ref) {
    Column column = columnsByAlias.get(ref.name());
    return column == null ? null : T2.of(this, column);
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
