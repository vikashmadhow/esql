/*
 * Copyright (c) 2018-2021 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.base.trie.PathTrie;
import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

/**
 * A composite type.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Struct extends Relation {
  public Struct(Context         context,
                UUID            id,
                String          name,
                String          displayName,
                String          description,
                List<Attribute> attributes,
                List<Column>    columns) {
    super(name);
    this.context = context;
    this.id = id;
    this.name = name;
    this.displayName = displayName;
    this.description = description;

    for (Column column: columns) {
      this.columns.add(column);
      this.columnsByAlias.put(column.name(), column);
    }

    if (attributes != null) {
      for (Attribute a: attributes) {
        attribute(a);
      }
    }
  }

  public Struct(Struct other) {
    super(other.name);
    this.context = other.context;
    this.id = other.id;
    this.name = other.name;
    this.displayName = other.displayName;
    this.description = other.description;
    for (Column column: other.columns) {
      this.columns.add(column.copy());
    }
  }

  @Override
  public Struct copy() {
    return new Struct(this);
  }

  @Override
  public String alias() {
    int pos = name.lastIndexOf('.');
    return pos == -1 ? name : name.substring(pos + 1);
  }

  @Override
  public Set<String> aliases() {
    return emptySet();
  }

  @Override
  public List<T2<Relation, Column>> columns() {
    return columns.stream().map(c -> new T2<Relation, Column>(this, c)).toList();
  }

  @Override
  public T2<Relation, Column> findColumn(ColumnRef ref) {
    Column column = columnsByAlias.get(ref.columnName());
    return column == null ? null : T2.of(this, column);
  }

  public void addColumn(Column c) {
    this.columns.add(c);
    this.columnsByAlias.put(c.name(), c);
  }

  public void addOrReplaceColumn(Column column) {
    String columnName = column.name();
    if (columnsByAlias.get(columnName) != null) {
      removeColumn(columnName);
    }
    addColumn(column);
  }

  public boolean removeColumn(String columnName) {
    List<T2<String, Column>> cols = columnsByAlias.getPrefixed(columnName);
    columnsByAlias.deletePrefixed(columnName);
    Set<String> colNames = cols.stream().map(T2::a).collect(toSet());
    return columns.removeIf(c -> colNames.contains(c.name()));
  }

  @Override
  public List<T2<Relation, Column>> columns(String prefix) {
    List<T2<String, Column>> cols = columnsByAlias.getPrefixed(prefix);
    return cols.stream()
               .map(t -> new T2<Relation, Column>(this, t.b().copy()))
               .toList();
  }

  public Attribute attribute(String name, Expression<?, String> expr) {
    return attribute(new Attribute(context, name, expr));
  }

  public UUID id() {
    return id;
  }

  public Struct id(UUID id) {
    return new Struct(context,
                      id,
                      name,
                      displayName,
                      description,
                      attributesList(),
                      columns);
  }

  public final Context context;

  /**
   * Internal unique identifier for struct in core tables.
   */
  public final UUID id;

  public final String displayName;

  public final String description;

  /**
   * Relation columns.
   */
  protected final List<Column> columns = new ArrayList<>();

  protected final PathTrie<Column> columnsByAlias = new PathTrie<>();
}