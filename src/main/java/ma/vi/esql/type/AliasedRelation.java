/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.type;

import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.query.Column;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static ma.vi.esql.parser.expression.ColumnRef.qualify;

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
    if (!copying()) {
      try {
        copying(true);
        return new AliasedRelation(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Relation forAlias(String alias) {
    return alias.equals(this.alias) ? relation : null;
  }

  @Override
  public List<Column> columns() {
    if (columns == null) {
      columns = relation.columns()
                        .stream()
                        .map(c -> qualify(c.copy(), alias, null, true))
                        .collect(toList());
    }
    return columns;
  }

  @Override
  public List<Column> columns(String relationAlias, String prefix) {
    return relation.columns(relationAlias, prefix)
                   .stream()
                   .map(c -> qualify(c, alias, null, true))
                   .collect(toList());
  }

  @Override
  public Column findColumn(String relationAlias, String name) {
    return relation.findColumn(relationAlias, name);
  }

  @Override
  public Expression<?> attribute(String name, Expression<?> value) {
    return relation.attribute(name, value);
  }

  @Override
  public Expression<?> attribute(String name) {
    return relation.attribute(name);
  }

  @Override
  public void attributes(Map<String, Expression<?>> attributes) {
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
