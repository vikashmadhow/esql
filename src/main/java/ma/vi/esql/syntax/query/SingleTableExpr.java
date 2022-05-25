/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.lang.NotFoundException;
import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.database.Structure;
import ma.vi.esql.exec.Filter;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.*;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.comparison.Equality;
import ma.vi.esql.syntax.expression.logical.And;
import ma.vi.esql.syntax.macro.Macro;
import ma.vi.esql.translation.TranslationException;
import org.pcollections.PMap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static ma.vi.base.string.Strings.makeUniqueSeq;
import static ma.vi.esql.semantic.type.Type.unqualifiedName;

/**
 * Represents a single table in the from clause; a single table can either refer
 * to a table in the database or a common table expression (CTE) defined in a
 * `with` query.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SingleTableExpr extends AbstractAliasTableExpr {
  public SingleTableExpr(Context context,
                         String tableName,
                         String alias) {
    super(context, "SingleTable",
          alias == null ? unqualifiedName(tableName) : alias,
          T2.of("table", new Esql<>(context, tableName)));
  }

  public SingleTableExpr(SingleTableExpr other) {
    super(other);
  }

  @SafeVarargs
  public SingleTableExpr(SingleTableExpr other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public SingleTableExpr copy() {
    return new SingleTableExpr(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public SingleTableExpr copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new SingleTableExpr(this, value, children);
  }

  @Override
  public ShortestPath findShortestPath(Filter filter) {
    if (context.structure.relationExists(tableName())) {
      BaseRelation rel = context.structure.relation(tableName());
      BaseRelation.Path path = rel.path(filter.table());
      return path == null ? null
                          : new ShortestPath(this, path, filter);
    }
    return null;
  }

  @Override
  public AppliedShortestPath applyShortestPath(ShortestPath shortest, TableExpr root) {
    if (shortest.source() == this) {
      Set<String> aliases = new HashSet<>(root.aliases());
      String lastAlias = shortest.filter().alias();
      if (lastAlias == null) {
        lastAlias = Type.unqualifiedName(shortest.filter().table());
      }
      lastAlias = makeUniqueSeq(aliases, lastAlias);

      String targetAlias;
      String sourceAlias = alias();
      TableExpr pathJoin = this;
      for (Iterator<BaseRelation.Link> i = shortest.path().elements().iterator();
           i.hasNext();) {
        BaseRelation.Link link = i.next();
        String target = link.reverse() ? link.constraint().table()
                                       : link.constraint().targetTable();
        if (!i.hasNext()) {
          /*
           * filter table (last table in path): use specified alias
           */
          targetAlias = lastAlias != null
                      ? lastAlias
                      : makeUniqueSeq(aliases, unqualifiedName(target)); // "t" + Strings.random();
        } else {
          targetAlias = makeUniqueSeq(aliases, unqualifiedName(target)); // "t" + Strings.random();
        }

        Expression<?, String> on = joinLink(link, sourceAlias, targetAlias, 0);
        if (link.constraint().sourceColumns().size() > 1) {
          for (int j = 1; j < link.constraint().sourceColumns().size(); j++) {
            on = new And(context, on, joinLink(link, sourceAlias, targetAlias, j));
          }
        }
        pathJoin = new JoinTableExpr(context,
                                     null,
                                     false,
                                     pathJoin,
                                     new SingleTableExpr(context,
                                                         link.reverse()
                                                       ? link.constraint().table()
                                                       : link.constraint().targetTable(),
                                                         targetAlias),
                                     on);
        sourceAlias = targetAlias;
      }
      if (shortest.path().elements().isEmpty()) {
        /*
         * If the shortest path is empty, it means that this table is the target
         * table, in which case the alias for the target table should be the alias
         * of this table as well.
         */
        lastAlias = alias();
      }
      return new AppliedShortestPath(shortest, pathJoin, lastAlias);
    }
    return null;
  }

  /**
   * Links can be in reverse path. (foreign key from target to source). E.g.
   * Com c <- Emp e:
   *    from Com c join Emp t on c._id (target column in link)=e.com_id (source column in link)
   * Or normal path (foreign key from source to target).
   * E.g. Emp e -> Com c:
   *    from Emp e join Com t on e.company_id=t._id
   */
  private Equality joinLink(BaseRelation.Link link,
                            String            sourceAlias,
                            String            targetAlias,
                            int               colIndex) {
    return new Equality(
      context,
      new ColumnRef(context, sourceAlias,
                    link.reverse() ? link.constraint().targetColumns().get(colIndex)
                                   : link.constraint().sourceColumns().get(colIndex)),
      new ColumnRef(context, targetAlias,
                    link.reverse() ? link.constraint().sourceColumns().get(colIndex)
                                   : link.constraint().targetColumns().get(colIndex)));
  }

  @Override
  public boolean exists(EsqlPath path) {
    if (context.type(tableName()) != null) {
      return true;
    } else {
      With with = path.ancestor(With.class);
      if (with != null) {
        for (Cte cte: with.ctes()) {
          if (cte.name().equals(tableName())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public TableExpr aliased(String name) {
    return name == null || name.equals(alias()) ? this : null;
  }

  @Override
  public List<Column> columnList(EsqlPath path) {
    String table = tableName();
    Structure structure = context.structure;
    if (structure.relationExists(table)) {
      return structure.relation(table)
                      .columns().stream()
                      .map(c -> new Column(c.b.context,
                                           c.b.name(),
                                           ColumnRef.qualify(c.b.expression(), alias()),
                                           c.b.type(),
                                           ColumnRef.qualify(c.b.metadata(), alias())))
                      .toList();
    } else {
      /*
       * Check CTE with this table name if in a 'with' query.
       */
      With with = path.ancestor(With.class);
      if (with != null) {
        for (Cte cte: with.ctes()) {
          if (cte.name().equals(table)) {
            return cte.columns().stream()
                      .map(c -> new Column(c.context, c.name(),
                                           new ColumnRef(c.context, alias(), c.name()),
                                           c.type(), null))
                      .toList();
          }
        }
      }
    }
    throw new NotFoundException(table + " is not a known relation (not a base relation or defined in this query)");
  }

  @Override
  public Relation computeType(EsqlPath path) {
    if (type == Types.UnknownType) {
      String table = tableName();
      Type t = context.type(table);
      if (t == null) {
        /*
         * for 'with' queries, ensure that CTEs have been added to local type registry
         * before throwing an exception
         */
        With with = path.ancestor(With.class);
        if (with != null) {
          for (Cte cte: with.ctes()) {
            if (cte.name().equals(table)) {
              cte.computeType(path.add(cte));
            }
          }
        }
      }
      t = context.type(table);
      if (t == null) {
        return Selection.UNKNOWN;
      }
      if (!(t instanceof Relation)) {
        throw new TranslationException(this, table + " is not a Relation. It is a " + t);
      }
      if (t instanceof AliasedRelation ar) {
        if (!ar.alias.equals(alias())) {
          t = new AliasedRelation(ar.relation, alias());
        }
      } else {
        t = new AliasedRelation((Relation)t, alias());
      }
      if (path.hasAncestor(Macro.OngoingMacroExpansion.class)) {
        context.type(alias(), t);
        return (AliasedRelation)t;
      } else {
        type = t;
      }
      context.type(alias(), type);
    }
    return (AliasedRelation)type;
  }

  @Override
  protected String trans(Target target, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env) {
    String table = tableName();
    String alias = alias();
    if (target == Target.ESQL) {
      return (alias == null ? "" : alias + ':') + (table == null ? "" : table);
    } else {
      String dbName;
      if (context.type(table) instanceof Selection
       || (context.type(table) instanceof AliasedRelation ar
        && ar.relation instanceof Selection)) {
        dbName = '"' + table + '"';
      } else {
        dbName = Type.dbTableName(table, target);
      }
      return dbName + (alias == null ? "" : " \"" + alias + '"');
    }
  }

  @Override
  public String toString() {
    String alias = alias();
    return (alias != null ? alias + ':' : "") + tableName();
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append(alias() != null ? alias() + ':' : "").append(tableName());
  }

  public String schema() {
    String table = tableName();
    if (table == null) {
      return null;
    } else {
      int pos = table.lastIndexOf('.');
      return pos == -1 ? null : table.substring(0, pos);
    }
  }

  public String tableOnly() {
    String table = tableName();
    if (table == null) {
      return null;
    } else {
      int pos = table.lastIndexOf('.');
      return pos == -1 ? table : table.substring(pos + 1);
    }
  }

  public String tableName() {
    return childValue("table");
  }
}
