/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.type;

import ma.vi.base.lang.NotFoundException;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.query.Column;

import java.util.ArrayList;
import java.util.List;
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
    if (!copying()) {
      try {
        copying(true);
        return new Join(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Relation forAlias(String alias) throws NotFoundException {
    if (alias == null) {
      return null;
    } else {
      Relation rel = left.forAlias(alias);
      return rel != null ? rel : right.forAlias(alias);
    }
  }

  @Override
  public List<Column> columns() {
    if (columns == null) {
      columns = new ArrayList<>();
      columns.addAll(left.columns());
      columns.addAll(right.columns());
    }
    return columns;
  }

  @Override
  protected Column findColumn(String relationAlias, String name) throws NotFoundException, AmbiguousColumnException {
    if (relationAlias == null) {
      Column col = left.column(name);
      if (col == null) {
        col = right.column(name);
      } else {
        Column rightCol = right.column(name);
        if (rightCol != null) {
          /*
           * Ambiguous column as existing in both left
           * and right relations.
           */
          throw new AmbiguousColumnException("Ambiguous column " + name +
              " exists in both " + left + " and " + right);
        }
      }
      return col;
    } else {
      Relation rel = forAlias(relationAlias);
      if (rel == null) {
        throw new NotFoundException("Relation with alias " + relationAlias + " could not be found");
      }
      return rel.findColumn(relationAlias, name);
//      for (Column c: rel.columns()) {
//        if (c.alias() != null && c.alias().equals(name)) {
//          return c;
//        }
//      }
//      return null;
    }
  }

  @Override
  public List<Column> columnsPrefixedBy(String relationAlias, String prefix) {
    List<Column> cols = new ArrayList<>(left.columnsPrefixedBy(relationAlias, prefix));
    cols.addAll(right.columnsPrefixedBy(relationAlias, prefix));
    return cols;
  }

  @Override
  public ConcurrentMap<String, Expression<?>> attributes() {
    ConcurrentMap<String, Expression<?>> attributes = new ConcurrentHashMap<>();
    attributes.putAll(left.attributes());
    attributes.putAll(right.attributes());
    attributes.putAll(super.attributes());
    return attributes;
  }

  @Override
  public Expression<?> attribute(String name) {
    return attributes().containsKey(name)       ? attributes().get(name)       :
           right.attributes().containsKey(name) ? right.attributes().get(name) :
           left.attributes().getOrDefault(name, null);
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
  private volatile List<Column> columns;
}
