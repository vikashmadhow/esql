/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.base.tuple.T2;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.query.Column;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static ma.vi.esql.syntax.expression.ColumnRef.qualify;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class AliasedRelation extends Relation {
  public AliasedRelation(Relation relation, String alias) {
    super(relation.name());
    this.alias = alias;
    this.relation = relation;
  }

  public AliasedRelation(AliasedRelation other) {
    super(other);
    this.relation = other.relation.copy();
    this.alias = other.alias;
  }

  @Override
  public AliasedRelation copy() {
    return new AliasedRelation(this);
  }

  @Override
  public String alias() {
    return alias;
  }

  @Override
  public Set<String> aliases() {
    return Collections.singleton(alias);
  }

  @Override
  public List<T2<Relation, Column>> columns() {
    if (columns == null) {
      columns = relation.columns().stream()
                        .map(c -> T2.of(c.a(), qualify(c.b(), alias)))
                        .collect(toList());
    }
    return columns;
  }

  @Override
  public List<T2<Relation, Column>> columns(String prefix) {
    return relation.columns(prefix).stream()
                   .map(t -> new T2<Relation, Column>(this, qualify(t.b(), alias)))
                   .toList();
  }

  @Override
  public T2<Relation, Column> findColumn(ColumnRef ref) {
    if (ref.qualifier() == null || ref.qualifier().equals(alias)) {
      T2<Relation, Column> col = relation.findColumn(ref);
      if (col != null) {
        col = T2.of(this, col.b());
      }
      return col;
    } else {
      return null;
    }
  }

  @Override
  public Attribute attribute(Attribute att) {
    return relation.attribute(att);
  }

  @Override
  public Attribute attribute(String name) {
    return relation.attribute(name);
  }

  @Override
  public void attributes(Collection<Attribute> attributes) {
    relation.attributes(attributes);
  }

  @Override
  public void clearAttributes() {
    relation.clearAttributes();
  }

  public final Relation relation;

  public final String alias;

  private transient volatile List<T2<Relation, Column>> columns;
}
