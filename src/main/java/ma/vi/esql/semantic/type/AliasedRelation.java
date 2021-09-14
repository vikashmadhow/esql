/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.query.Column;

import java.util.List;
import java.util.Map;

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
  public Relation forAlias(String alias) {
    return this.alias.equals(alias) ? this : null;
  }

  @Override
  public List<Column> columns() {
    if (columns == null) {
      columns = relation.columns()
                        .stream()
                        .map(c -> qualify(c.copy(), alias, true))
                        .collect(toList());
    }
    return columns;
  }

  @Override
  public List<Column> columns(String relationAlias, String prefix) {
    return relation.columns(relationAlias, prefix)
                   .stream()
                   .map(c -> qualify(c, alias, true))
                   .collect(toList());
  }

  @Override
  public Column findColumn(String relationAlias, String name) {
    if (relationAlias == null || relationAlias.equals(alias)) {
      Column col = relation.findColumn(null, name);
      if (col != null) {
        col = qualify(col.copy(), alias, true);
      }
      return col;
    } else {
      return null;
    }
  }

  @Override
  public Expression<?, String> attribute(String name, Expression<?, String> value) {
    return relation.attribute(name, value);
  }

  @Override
  public Expression<?, String> attribute(String name) {
    return relation.attribute(name);
  }

  @Override
  public void attributes(Map<String, Expression<?, String>> attributes) {
    relation.attributes(attributes);
  }

  @Override
  public void clearAttributes() {
    relation.clearAttributes();
  }

  public final Relation relation;

  public final String alias;

  private transient volatile List<Column> columns;
}
