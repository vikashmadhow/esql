/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.lang.NotFoundException;
import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Structure;
import ma.vi.esql.semantic.type.*;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.macro.Macro;
import ma.vi.esql.translation.TranslationException;
import org.pcollections.PMap;

import java.util.List;

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
          alias == null ? Type.unqualifiedName(tableName) : alias,
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
  public TableExpr named(String name) {
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
            if (cte.fields() == null || cte.fields().isEmpty()) {
              return cte.columns().stream()
                        .map(c -> c.expression(ColumnRef.qualify(c.expression(), alias())))
                        .toList();
            } else {
              return cte.columns().stream()
                        .map(c -> new Column(c.context, c.name(),
                                             new ColumnRef(c.context, table, c.name()),
                                             c.type(), null))
                        .toList();
            }
          }
        }
      }
    }
    throw new NotFoundException(table + " is not a known relation (not a base relation or defined in this query)");
  }

  @Override
  public AliasedRelation computeType(EsqlPath path) {
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
        throw new NotFoundException(table + " is not a known relation in this query");
      }
      if (!(t instanceof Relation)) {
        throw new TranslationException(table + " is not a Relation. It is a " + t);
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
  protected String trans(Target target, EsqlPath path, PMap<String, Object> parameters) {
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
