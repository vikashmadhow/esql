/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.lang.NotFoundException;
import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.*;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

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
    super(context,
          tableName,
          alias == null ? Type.unqualifiedName(tableName) : alias);
  }

  public SingleTableExpr(SingleTableExpr other) {
    super(other);
  }

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
  public AliasedRelation type(EsqlPath path) {
//    if (type == null) {
//      type = new AliasedRelation(relation(), alias());
////      type = (BaseRelation)t;
//    }
//    return type;

    if (type == null) {
      String table = tableName();
      Type t = context.type(table);
      if (t instanceof BaseRelation) {
        type = new AliasedRelation(relation(), alias());
      } else {
        /*
         * for 'with' queries, ensure that CTEs have been added to local type registry
         * before throwing an exception
         */
        With with = path.ancestor(With.class);
        if (with != null) {
          for (Cte cte: with.ctes()) {
            cte.type(path);
          }
        }
        t = context.type(table);
        if (t == null) {
          throw new NotFoundException(table + " is not known relation in this query");
        }
        type = new AliasedRelation((Relation)t, alias());
      }
    }
    return type;
  }

  public BaseRelation relation() {
    Type t = context.type(tableName());
    if (t == null) {
      throw new NotFoundException(tableName() + " is not a known relation " +
          "in this query");
    } else if (!(t instanceof BaseRelation)) {
      throw new NotFoundException(tableName() + " is not a base relation " +
          "in this query. It is a " + t.getClass().getSimpleName());
    }
    return (BaseRelation)t;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    String table = tableName();
    String alias = alias();
    if (target == Target.ESQL) {
      return (alias == null ? "" : alias + ':') + (table == null ? "" : table);
    } else {
      String dbName;
      if (table.indexOf('.') == -1 && context.type(table) instanceof Selection) {
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
    return value;
  }

  private transient volatile AliasedRelation type;
}
