/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.base.lang.NotFoundException;
import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.expression.ColumnRef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Join extends Relation {
  public Join(Relation left, Relation right) {
    super("join(" + left.name() + ", " + right.name() + ")");
    this.left = left;
    this.right = right;
  }

  public Join(Join other) {
    super(other);
    this.left = other.left.copy();
    this.right = other.right.copy();
  }

  @Override
  public Join copy() {
    return new Join(this);
  }

  @Override
  public String alias() {
    return null;
  }

  @Override
  public Set<String> aliases() {
    Set<String> aliases = new HashSet<>(left.aliases());
    aliases.addAll(right.aliases());
    return aliases;
  }

  @Override
  public List<T2<Relation, Column>> columns() {
    if (columns == null) {
      columns = new ArrayList<>(left.columns());
      columns.addAll(right.columns());
    }
    return columns;
  }

  @Override
  public List<T2<Relation, Column>> columns(String prefix) {
    List<T2<Relation, Column>> cols = new ArrayList<>(left.columns(prefix));
    cols.addAll(right.columns(prefix));
    return cols;
  }

  @Override
  public T2<Relation, Column> findColumn(ColumnRef ref) throws NotFoundException, AmbiguousColumnException {
    T2<Relation, Column> col = left.findColumn(ref);
    if (col == null) {
      return right.findColumn(ref);
    } else {
      T2<Relation, Column> rightCol = right.findColumn(ref);
      if (rightCol != null) {
        throw new AmbiguousColumnException("Ambiguous column " + ref + " exists in both "
                                         + left.name() + " and " + right.name());
      }
      return col;
    }
  }

  @Override
  public ConcurrentMap<String, Attribute> attributes() {
    ConcurrentMap<String, Attribute> attributes = new ConcurrentHashMap<>();
    attributes.putAll(left.attributes());
    attributes.putAll(right.attributes());
    attributes.putAll(super.attributes());
    return attributes;
  }

  @Override
  public Attribute attribute(String name) {
    return attributes().containsKey(name)       ? attributes().get(name)
         : right.attributes().containsKey(name) ? right.attributes().get(name)
         : left.attributes().getOrDefault(name, null);
  }

  @Override
  public void clearAttributes() {
    super.clearAttributes();
    left.clearAttributes();
    right.clearAttributes();
    super.clearAttributes();
  }

  private final Relation left;
  private final Relation right;
  private transient volatile List<T2<Relation, Column>> columns;
}