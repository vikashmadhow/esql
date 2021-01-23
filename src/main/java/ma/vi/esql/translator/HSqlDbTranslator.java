package ma.vi.esql.translator;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.define.PrimaryKeyConstraint;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.*;
import ma.vi.esql.type.BaseRelation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;
import static ma.vi.esql.parser.modify.Delete.findSingleTable;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class HSqlDbTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.HSQLDB;
  }

  @Override
  protected QueryTranslation translate(Select select) {
    StringBuilder st = new StringBuilder("select ");
    if (select.distinct()) {
      st.append("distinct ");
      List<Expression<?>> distinctOn = select.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        st.append('(')
          .append(distinctOn.stream().map(e -> e.translate(target())).collect(joining(", ")))
          .append(") ");
      }
    }

    // add output clause
    QueryTranslation q = select.constructResult(st, target(), null,
                                                true, true);
    if (select.tables() != null) {
      st.append(" from ").append(select.tables().translate(target()));
    }
    if (select.where() != null) {
      st.append(" where ").append(select.where().translate(target()));
    }
    if (select.groupBy() != null) {
      st.append(select.groupBy().translate(target()));
    }
    if (select.having() != null) {
      st.append(" having ").append(select.having().translate(target()));
    }
    if (select.orderBy() != null && !select.orderBy().isEmpty()) {
      st.append(" order by ")
        .append(select.orderBy().stream()
                      .map(e -> e.translate(target()))
                      .collect(joining(", ")));
    }
    if (select.offset() != null) {
      st.append(" offset ").append(select.offset().translate(target()));
    }
    if (select.limit() != null) {
      st.append(" limit ").append(select.limit().translate(target()));
    }
    return new QueryTranslation(st.toString(),
                                q.columns,
                                q.columnToIndex,
                                q.resultAttributeIndices,
                                q.resultAttributes);
  }

  @Override
  protected QueryTranslation translate(Update update) {
    StringBuilder st = new StringBuilder("update ");

    TableExpr from = update.tables();
    if (!(from instanceof SingleTableExpr)) {
      throw new TranslationException(target() + " does not support multiple tables or joins in updates");
    }
    st.append(from.translate(target()));
    Update.addSet(st, update.set(), target(), false);

    if (update.where() != null) {
      st.append(" where ").append(update.where().translate(target()));
    }
    if (update.columns() != null) {
      throw new TranslationException(target() + " does not support return values in updates");
    }
    return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                emptyList(), emptyMap());
  }

//  @Override
//  protected QueryTranslation translate(Update update) {
//    TableExpr from = update.tables();
//    if (from instanceof SingleTableExpr) {
//      StringBuilder st = new StringBuilder("update ");
//      st.append(from.translate(target()));
//      Update.addSet(st, update.set(), target(), false);
//      if (update.where() != null) {
//        st.append(" where ").append(update.where().translate(target()));
//      }
//      if (update.columns() != null) {
//        throw new TranslationException(target() + " does not support return values in updates");
//      }
//      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
//                                  emptyList(), emptyMap());
//
//    } else if (from instanceof AbstractJoinTableExpr) {
//      /*
//       * Since HSQLDB does not support update using multiple tables, we simulate
//       * it by changing the update into select and executing it in a with query
//       * whose result is then used to perform the update. The update is linked
//       * to the select using the primary key on the main table. For instance:
//       *
//       *      update usr_role
//       *        from usr:_platform.user.User
//       *        join usr_role:_platform.user.UserRole on usr_role.user_id=usr._id
//       *        join role:_platform.user.Role on usr_role.role_id=role._id
//       *         set role_id=role._id
//       *       where usr.email='xyz@yxz.com'
//       *      return usr_role.user_id
//       *
//       * becomes:
//       *
//       *      with upd(id, v1) as (
//       *        select usr_role._id, role._id
//       *          from _platform.user.User usr
//       *          join _platform.user.UserRole usr_role on usr_role.user_id = usr._id
//       *          join _platform.user.Role role on usr_role.role_id = role._id
//       *        where usr.email = 'xyz@yxz.com'
//       *      )
//       *      update _platform.user.UserRole usr_role
//       *         set role_id=upd.v1
//       *        from upd
//       *       where usr_role._id=upd.id
//       *      returning usr_role.user_id
//       */
//      SingleTableExpr updateTable = findSingleTable((AbstractJoinTableExpr)from,
//                                                    update.updateTableAlias());
//      if (updateTable == null) {
//        throw new TranslationException("Could not find table with alias " + update.updateTableAlias());
//      }
//      BaseRelation rel = updateTable.relation();
//      PrimaryKeyConstraint pk = rel.primaryKey();
//      if (pk == null) {
//        throw new TranslationException("Table " + rel + " does not have a primary key, which is "
//                                     + "needed to simulate updates using multiple tables on HSQLDB.");
//      }
//      List<String> pkCols = pk.columns();
//
//      String withAlias = "\"!!\"";
//      StringBuilder st =
//          new StringBuilder("with " + withAlias + "("
//                                + IntStream.rangeClosed(1, pkCols.size())
//                                           .boxed()
//                                           .map(i -> "id" + i)
//                                           .collect(joining(", "))
//                                + ", "
//                                + IntStream.rangeClosed(1, update.set().attributes().size())
//                                           .boxed()
//                                           .map(i -> "v" + i)
//                                           .collect(joining(", "))
//                                + ") as (");
//
//      List<Attribute> set = new ArrayList<>(update.set().attributes().values());
//      st.append("select ")
//        .append(pkCols.stream()
//                      .map(k -> '"' + update.updateTableAlias() + "\".\"" + k + '"')
//                      .collect(joining(", ")))
//        .append(", ")
//        .append(set.stream()
//                   .map(a -> a.attributeValue().translate(target()))
//                   .collect(joining(", ")))
//        .append(" from ").append(from.translate(target()));
//
//      if (update.where() != null) {
//        st.append(" where ").append(update.where().translate(target()));
//      }
//      st.append(") update ")
//        .append(updateTable.translate(target()))
//        .append(" set ");
//      boolean first = true;
//      for (int i = 0; i < set.size(); i++) {
//        if (first) { first = false;   }
//        else       { st.append(", "); }
//        Attribute a = set.get(i);
//        st.append(a.name()).append('=').append(withAlias).append(".v").append(i+1);
//      }
//
//      st.append(" from ").append(withAlias).append(" where ");
//      for (int i = 0; i < pkCols.size(); i++) {
//        if (i > 0) {
//          st.append(" and ");
//        }
//        st.append('"').append(update.updateTableAlias())
//          .append("\".\"").append(pkCols.get(i))
//          .append("\"=").append(withAlias).append(".id").append(i+1);
//      }
//
//      if (update.columns() != null) {
//        throw new TranslationException(target() + " does not support return values in updates");
//      }
//      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
//                                  emptyList(), emptyMap());
//    } else {
//      throw new TranslationException("Wrong table type to update: " + from);
//    }
//  }

  @Override
  protected QueryTranslation translate(Delete delete) {
    StringBuilder st = new StringBuilder("delete ");
    TableExpr from = delete.tables();
    if (!(from instanceof SingleTableExpr)) {
      throw new TranslationException(target() + " does not support multiple tables or joins in updates");
    }
    st.append(" from ").append(from.translate(target()));
    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target()));
    }
    if (delete.columns() != null) {
      throw new TranslationException(target() + " does not support returning rows in deletes");
    }
    return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                emptyList(), emptyMap());
  }
}
