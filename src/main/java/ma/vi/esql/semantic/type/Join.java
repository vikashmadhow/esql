/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.base.lang.NotFoundException;
import ma.vi.base.string.Strings;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.query.Column;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.stream.Collectors.toList;

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
  public Relation forAlias(String alias) throws NotFoundException {
    if (alias == null) {
      return null;
    } else {
      Relation rel = left.forAlias(alias);
      return rel != null ? rel : right.forAlias(alias);
    }
  }

  @Override
  public Set<String> aliases() {
    Set<String> aliases = new HashSet<>(left.aliases());
    aliases.addAll(right.aliases());
    return aliases;
  }

  @Override
  public List<Column> columns() {
    if (columns == null) {
      columns = new ArrayList<>(left.columns());
      columns.addAll(right.columns());
    }
    return columns;
  }

  @Override
  public List<Column> columns(String alias, String prefix) {
    List<Column> cols = new ArrayList<>();
    Set<String> colNames = new HashSet<>();

    Relation leftRel = alias == null ? left : left.forAlias(alias);
    if (leftRel != null) {
      cols.addAll(leftRel.columns(alias, prefix));
      colNames.addAll(cols.stream().map(Column::alias).toList());
    }

    Relation rightRel = alias == null ? right : right.forAlias(alias);
    if (rightRel != null) {
      for (Column col: rightRel.columns(alias, prefix)) {
        if (!colNames.contains(col.alias())) {
          cols.add(col);
          colNames.add(col.alias());
        } else {
          if (alias == null && !col.alias().startsWith("/")) {
            /*
             * Ambiguous column as existing in both left and right relations.
             */
            throw new AmbiguousColumnException("Ambiguous column " + col.alias() + " exists in both "
                                                   + left.name() + " and " + right.name());

          } else {
            String newName = Strings.makeUnique(colNames, col.alias());
            col.alias(newName);
            cols.add(col);
          }
        }
      }
    }
    return cols;
  }

  @Override
  public Column findColumn(String relationAlias, String name) throws NotFoundException, AmbiguousColumnException {
    if (relationAlias == null) {
      Column col = left.findColumn(relationAlias, name);
      if (col == null) {
        col = right.findColumn(relationAlias, name);
      } else {
        Column rightCol = right.findColumn(relationAlias, name);
        if (rightCol != null) {
          /*
           * Ambiguous column as existing in both left and right relations.
           */
          throw new AmbiguousColumnException("Ambiguous column " + name + " exists in both "
                                           + left.name() + " and " + right.name());
        }
      }
      return col;
    } else {
      Relation rel = forAlias(relationAlias);
      if (rel == null) {
        return null;
      }
      return rel.findColumn(relationAlias, name);
    }
  }

  @Override
  public ConcurrentMap<String, Expression<?, String>> attributes() {
    ConcurrentMap<String, Expression<?, String>> attributes = new ConcurrentHashMap<>();
    attributes.putAll(left.attributes());
    attributes.putAll(right.attributes());
    attributes.putAll(super.attributes());
    return attributes;
  }

  @Override
  public Expression<?, String> attribute(String name) {
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
  private transient volatile List<Column> columns;
}
