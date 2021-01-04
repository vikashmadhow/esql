/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.base.lang.NotFoundException;
import ma.vi.esql.parser.Context;
import ma.vi.esql.type.AliasedRelation;
import ma.vi.esql.type.BaseRelation;
import ma.vi.esql.type.Relation;
import ma.vi.esql.type.Type;

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

  @Override
  public SingleTableExpr copy() {
    if (!copying()) {
      try {
        copying(true);
        return new SingleTableExpr(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public BaseRelation type() {
    if (type == null) {
      Type t = context.type(tableName());
      if (t == null) {
        throw new NotFoundException(tableName() + " is not a known relation " +
            "in this query");
      } else if (!(t instanceof BaseRelation)) {
        throw new NotFoundException(tableName() + " is not a base relation " +
            "in this query. It is a " + t.getClass().getSimpleName());
      }
//      type = new AliasedRelation((Relation)t, alias());
      type = (BaseRelation)t;
    }
    return type;

//    if (type == null) {
//      String table = tableName();
//      Relation r = (Relation)context.typeOf(table);
//      if (r == null) {
//        r = context.translator.structure().relation(table);
//      }
//      if (r == null) {
//        // for 'with' queries, ensure that CTEs have been added to local type registry
//        // before throwing an exception
//        With with = ancestor(With.class);
//        if (with != null) {
//          for (Cte cte: with.ctes()) {
//            cte.type();
//          }
//        }
//        r = (Relation)context.typeOf(table);
//        if (r == null) {
//          throw new TranslationException("Relation " + table + " is not known");
//        }
//      }
//      type = new AliasedRelation(context.translator, r.copy(), alias());
//    }
//    return type;
  }

  @Override
  public String translate(Target target) {
    String table = tableName();
    String alias = alias();
    if (target == Target.ESQL) {
      return (alias == null ? "" : alias + ':') + (table == null ? "" : table);
    } else {
      return (table == null ? "" : Type.dbTableName(table, target)) + (alias == null ? "" : " \"" + alias + '"');
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

  private transient volatile BaseRelation type;
}
