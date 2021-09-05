package ma.vi.esql.translator;

import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.TranslationException;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.syntax.modify.InsertRow;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.query.QueryTranslation;
import ma.vi.esql.syntax.query.Select;
import ma.vi.esql.syntax.query.SingleTableExpr;
import ma.vi.esql.syntax.query.TableExpr;
import ma.vi.esql.semantic.type.Type;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class HSqlDbTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.HSQLDB;
  }

  @Override
  protected QueryTranslation translate(Select select, EsqlPath path, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("select ");
    if (select.distinct()) {
      st.append("distinct ");
      List<Expression<?, String>> distinctOn = select.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        st.append('(')
          .append(distinctOn.stream().map(e -> e.translate(target(), path.add(e), parameters)).collect(joining(", ")))
          .append(") ");
      }
    }

    // add output clause
    QueryTranslation q = select.constructResult(st, target(), null, parameters);
    if (select.tables() != null) {
      st.append(" from ").append(select.tables().translate(target(), path.add(select.tables()), parameters));
    }
    if (select.where() != null) {
      st.append(" where ").append(select.where().translate(target(), path.add(select.where()), parameters));
    }
    if (select.groupBy() != null) {
      st.append(select.groupBy().translate(target(), path.add(select.groupBy()), parameters));
    }
    if (select.having() != null) {
      st.append(" having ").append(select.having().translate(target(), path.add(select.having()), parameters));
    }
    if (select.orderBy() != null && !select.orderBy().isEmpty()) {
      st.append(" order by ")
        .append(select.orderBy().stream()
                      .map(e -> e.translate(target(), path.add(e), parameters))
                      .collect(joining(", ")));
    }
    if (select.offset() != null) {
      st.append(" offset ").append(select.offset().translate(target(), path.add(select.offset()), parameters));
    }
    if (select.limit() != null) {
      st.append(" limit ").append(select.limit().translate(target(), path.add(select.limit()), parameters));
    }
    return new QueryTranslation(st.toString(),
                                q.columns,
                                q.columnToIndex,
                                q.resultAttributeIndices,
                                q.resultAttributes);
  }

  @Override
  protected QueryTranslation translate(Update update, EsqlPath path, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("update ");

    TableExpr from = update.tables();
    if (!(from instanceof SingleTableExpr)) {
      throw new TranslationException(target() + " does not support multiple tables or joins in updates");
    }
    st.append(from.translate(target(), path.add(from), parameters));
    Update.addSet(st, update.set(), target(), false);

    if (update.where() != null) {
      st.append(" where ").append(update.where().translate(target(), path.add(update.where()), parameters));
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
//      st.append(from.translate(target(), parameters));
//      Update.addSet(st, update.set(), target(), false);
//      if (update.where() != null) {
//        st.append(" where ").append(update.where().translate(target(), parameters));
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
//                   .map(a -> a.attributeValue().translate(target(), parameters))
//                   .collect(joining(", ")))
//        .append(" from ").append(from.translate(target(), parameters));
//
//      if (update.where() != null) {
//        st.append(" where ").append(update.where().translate(target(), parameters));
//      }
//      st.append(") update ")
//        .append(updateTable.translate(target(), parameters))
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
  protected QueryTranslation translate(Delete delete, EsqlPath path, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("delete ");
    TableExpr from = delete.tables();
    if (!(from instanceof SingleTableExpr)) {
      throw new TranslationException(target() + " does not support multiple tables or joins in updates");
    }
    st.append(" from ").append(from.translate(target(), path.add(from), parameters));
    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target(), path.add(delete.where()), parameters));
    }
    if (delete.columns() != null) {
      throw new TranslationException(target() + " does not support returning rows in deletes");
    }
    return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                emptyList(), emptyMap());
  }

  @Override
  protected QueryTranslation translate(Insert insert, EsqlPath path, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("insert into ");
    TableExpr table = insert.tables();
    if (!(table instanceof SingleTableExpr)) {
      throw new TranslationException("Insert only works with single tables. A " + table.getClass().getSimpleName()
                                         + " was found instead.");
    }
    st.append(Type.dbTableName(((SingleTableExpr)table).tableName(), target()));

    List<String> fields = insert.fields();
    if (fields != null && !fields.isEmpty()) {
      st.append(fields.stream()
                      .map(f -> '"' + f + '"')
                      .collect(joining(", ", "(", ")")));
    }

    List<InsertRow> rows = insert.rows();
    if (rows != null && !rows.isEmpty()) {
      st.append(rows.stream()
                    .map(row -> row.translate(target(), path.add(row), parameters))
                    .collect(joining(", ", " values", "")));

    } else if (insert.defaultValues()) {
      st.append(" default values");

    } else {
      st.append(' ').append(insert.select().translate(target(), path.add(insert.select()), Map.of("addAttributes", false)).statement);
    }

    if (insert.columns() != null && !insert.columns().isEmpty()) {
      throw new TranslationException(target() + " does not support returning rows in inserts");
    }
    return new QueryTranslation(st.toString(), emptyList(), emptyMap(), emptyList(), emptyMap());
  }
}
