package ma.vi.esql.translation;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.syntax.modify.InsertRow;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.query.*;
import org.pcollections.PMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class PostgresqlTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.POSTGRESQL;
  }

  @Override
  protected QueryTranslation translate(Select               select,
                                       EsqlConnection       esqlCon,
                                       EsqlPath             path,
                                       PMap<String, Object> parameters,
                                       Environment          env) {
    StringBuilder st = new StringBuilder("select ");
    if (select.distinct()) {
      st.append("distinct ");
      List<Expression<?, ?>> distinctOn = select.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        st.append("on (")
          .append(distinctOn.stream()
                            .map(e -> e.translate(target(), esqlCon, path.add(e), parameters, env).toString())
                            .collect(joining(", ")))
          .append(") ");
      }
    }

    // add output clause
    QueryTranslation q = select.constructResult(st, target(), path, null, parameters, env);
    if (select.tables() != null) {
      st.append(" from ").append(select.tables().translate(target(), esqlCon, path.add(select.tables()), parameters, env));
    }
    if (select.where() != null) {
      st.append(" where ").append(select.where().translate(target(), esqlCon, path.add(select.where()), parameters, env));
    }
    if (select.groupBy() != null) {
      st.append(select.groupBy().translate(target(), esqlCon, path.add(select.groupBy()), parameters, env));
    }
    if (select.having() != null) {
      st.append(" having ").append(select.having().translate(target(), esqlCon, path.add(select.having()), parameters, env));
    }
    if (select.orderBy() != null && !select.orderBy().isEmpty()) {
      st.append(" order by ")
        .append(select.orderBy().stream()
                      .map(e -> e.translate(target(), esqlCon, path.add(e), parameters, env))
                      .collect(joining(", ")));
    }
    if (select.offset() != null) {
      st.append(" offset ").append(select.offset().translate(target(), esqlCon, path.add(select.offset()), parameters, env));
    }
    if (select.limit() != null) {
      st.append(" limit ").append(select.limit().translate(target(), esqlCon, path.add(select.limit()), parameters, env));
    }
    return new QueryTranslation(select,
                                st.toString(),
                                q.columns(),
                                q.resultAttributes());
  }

  @Override
  protected QueryTranslation translate(Update               update,
                                       EsqlConnection       esqlCon,
                                       EsqlPath             path,
                                       PMap<String, Object> parameters,
                                       Environment          env) {
    TableExpr from = update.tables();
    if (from instanceof SingleTableExpr) {
      StringBuilder st = new StringBuilder("update ");
      st.append(from.translate(target(), esqlCon, path.add(from), parameters, env));
      Util.addSet(st, update.set(), target(), false, path, env);
      if (update.where() != null) {
        st.append(" where ").append(update.where().translate(target(), esqlCon, path.add(update.where()), parameters, env));
      }
      QueryTranslation q = null;
      if (update.columns() != null && !update.columns().isEmpty()) {
        st.append(" returning ");
        q = update.constructResult(st, target(), path, null, parameters, env);
      }
      if (q == null) {
        return new QueryTranslation(update, st.toString(), emptyList(), emptyMap());
      } else {
        return new QueryTranslation(update,
                                    st.toString(),
                                    q.columns(),
                                    q.resultAttributes());
      }
    } else if (from instanceof AbstractJoinTableExpr) {
      /*
       * For postgresql the single table referred by the update table alias must
       * be extracted from the table expression and added as the main table of
       * the update statement; the rest of the table expression can then be added
       * to the `from` clause of the update; any join condition removed when
       * extracting the single update table must be moved to the `where` clause.
       * However, the extracted single table cannot then be part of a join condition,
       * which makes the rewriting of the query much more complicated.
       * 
       * A simpler approach is to change the update into select and execute it
       * in a with query whose result is then used to perform the update. For
       * instance:
       * 
       *      update usr_role
       *        from usr:_platform.user.User
       *        join usr_role:_platform.user.UserRole on usr_role.user_id=usr._id
       *        join role:_platform.user.Role on usr_role.role_id=role._id
       *         set role_id=role._id
       *       where usr.email='xyz@yxz.com'
       *      return usr_role.user_id
       * 
       * becomes:
       *
       *      with upd(id, v1) as (
       *        select usr_role.ctid, role._id
       *          from _platform.user.User usr
       *          join _platform.user.UserRole usr_role on usr_role.user_id = usr._id
       *          join _platform.user.Role role on usr_role.role_id = role._id
       *        where usr.email = 'xyz@yxz.com'
       *      )
       *      update _platform.user.UserRole usr_role
       *         set role_id=upd.v1
       *        from upd
       *       where usr_role.ctid=upd.id
       *      returning usr_role.user_id
       */
      String withAlias = "\"!!\"";
      StringBuilder st =
          new StringBuilder("with " + withAlias + "(id, "
                                    + IntStream.rangeClosed(1, update.set().sets().size())
                                               .boxed()
                                               .map(i -> "v" + i)
                                               .collect(joining(", "))
                                    + ") as (");

      List<Attribute> set = new ArrayList<>(update.set().sets().values());
      st.append("select \"").append(update.updateTableAlias()).append("\".ctid, ")
        .append(set.stream()
                   .map(a -> a.attributeValue().translate(target(), esqlCon, path.add(a.attributeValue()), parameters, env).toString())
                   .collect(joining(", ")))
        .append(" from ").append(from.translate(target(), esqlCon, path.add(from), parameters, env));

      if (update.where() != null) {
        st.append(" where ").append(update.where().translate(target(), esqlCon, path.add(update.where()), parameters, env));
      }
      st.append(") update ");

      SingleTableExpr updateTable = (SingleTableExpr)from.aliased(update.updateTableAlias());
      if (updateTable == null) {
        throw new TranslationException(update, "Could not find table with alias " + update.updateTableAlias());
      }
      st.append(updateTable.translate(target(), esqlCon, path.add(updateTable), parameters, env));

      st.append(" set ");
      boolean first = true;
      for (int i = 0; i < set.size(); i++) {
        if (first) { first = false;   }
        else       { st.append(", "); }
        Attribute a = set.get(i);
        String columnName = a.name();
        int pos = columnName.lastIndexOf('.');
        if (pos != -1) {
          columnName = columnName.substring(pos + 1);
        }
        st.append(columnName).append('=').append(withAlias).append(".v").append(i+1);
      }
      st.append(" from ").append(withAlias)
        .append(" where \"").append(update.updateTableAlias())
        .append("\".ctid=").append(withAlias).append(".id");

      QueryTranslation q = null;
      if (update.columns() != null && !update.columns().isEmpty()) {
        st.append(" returning ");
        q = update.constructResult(st, target(), path, null, parameters, env);
      }
      if (q == null) {
        return new QueryTranslation(update, st.toString(), emptyList(), emptyMap());
      } else {
        return new QueryTranslation(update,
                                    st.toString(),
                                    q.columns(),
                                    q.resultAttributes());
      }
    } else {
      throw new TranslationException(update, "Wrong table type to update: " + from);
    }
  }
  @Override
  protected QueryTranslation translate(Delete               delete,
                                       EsqlConnection       esqlCon,
                                       EsqlPath             path,
                                       PMap<String, Object> parameters,
                                       Environment          env) {
    TableExpr from = delete.tables();
    if (from instanceof SingleTableExpr) {
      StringBuilder st = new StringBuilder("delete from ");
      st.append(from.translate(target(), esqlCon, path.add(from), parameters, env));
      if (delete.where() != null) {
        st.append(" where ").append(delete.where().translate(target(), esqlCon, path.add(delete.where()), parameters, env));
      }
      QueryTranslation q = null;
      if (delete.columns() != null && !delete.columns().isEmpty()) {
        st.append(" returning ");
        q = delete.constructResult(st, target(), path, null, parameters, env);
      }
      if (q == null) {
        return new QueryTranslation(delete, st.toString(), emptyList(), emptyMap());
      } else {
        return new QueryTranslation(delete,
                                    st.toString(),
                                    q.columns(),
                                    q.resultAttributes());
      }
    } else if (from instanceof AbstractJoinTableExpr) {
      /*
       * For postgresql the single table referred by the delete table alias must
       * be extracted from the table expression and added as the main table of
       * the delete statement; the rest of the table expression can then be added
       * to the `using` clause of the update; any join condition removed when
       * extracting the single update table must be moved to the `where` clause.
       * However, the extracted single table cannot then be part of a join condition,
       * which makes the rewriting of the query much more complicated. Also more
       * complex join types are not supported.
       *
       * A simpler approach is to change the delete into a select and execute it
       * in a `with` query whose result is then used to perform the delete. For
       * instance:
       *
       *      delete ur
       *        from  u:_user.User
       *        join ur:_user.UserRole on ur.user_id=u._id
       *        join  r:_user.Role     on ur.role_id=r._id
       *       where u.email='xyz@yxz.com'
       *      return ur.user_id
       *
       * becomes:
       *
       *      with del(id) as (
       *        select ur.ctid
       *          from _user.User     u
       *          join _user.UserRole ur on ur.user_id = u._id
       *          join _user.Role     r  on ur.role_id = r._id
       *        where u.email = 'xyz@yxz.com'
       *      )
       *      delete from _user.UserRole ur
       *       using upd
       *       where ur.ctid=del.id
       *      returning ur.user_id
       */
      String withAlias = "\"!!\"";
      StringBuilder st = new StringBuilder("with " + withAlias + "(id) as (");
      st.append("select \"").append(delete.deleteTableAlias()).append("\".ctid ")
        .append(" from ").append(from.translate(target(), esqlCon, path.add(from), parameters, env));

      if (delete.where() != null) {
        st.append(" where ").append(delete.where().translate(target(), esqlCon, path.add(delete.where()), parameters, env));
      }
      st.append(") delete from ");

      SingleTableExpr deleteTable = (SingleTableExpr)from.aliased(delete.deleteTableAlias());
      if (deleteTable == null) {
        throw new TranslationException(delete, "Could not find table with alias " + delete.deleteTableAlias());
      }
      st.append(deleteTable.translate(target(), esqlCon, path.add(deleteTable), parameters, env));

      st.append(" using ").append(withAlias)
        .append(" where \"").append(delete.deleteTableAlias())
        .append("\".ctid=").append(withAlias).append(".id");

      QueryTranslation q = null;
      if (delete.columns() != null && !delete.columns().isEmpty()) {
        st.append(" returning ");
        q = delete.constructResult(st, target(), path, null, parameters, env);
      }
      if (q == null) {
        return new QueryTranslation(delete, st.toString(), emptyList(), emptyMap());
      } else {
        return new QueryTranslation(delete,
                                    st.toString(),
                                    q.columns(),
                                    q.resultAttributes());
      }
    } else {
      throw new TranslationException(delete, "Wrong table type to delete from: " + from);
    }
  }

  @Override
  protected QueryTranslation translate(Insert               insert,
                                       EsqlConnection       esqlCon,
                                       EsqlPath             path,
                                       PMap<String, Object> parameters,
                                       Environment          env) {
    StringBuilder st = new StringBuilder("insert into ");
    TableExpr table = insert.tables();
    if (!(table instanceof SingleTableExpr)) {
      throw new TranslationException(insert, "Insert only works with single tables. A " + table.getClass().getSimpleName()
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
                    .map(row -> row.translate(target(), esqlCon, path.add(row), parameters, env))
                    .collect(joining(", ", " values", "")));

    } else if (insert.defaultValues()) {
      st.append(" default values");

    } else {
      st.append(' ').append(insert.select().translate(target(),
                                                      esqlCon,
                                                      path.add(insert.select()),
                                                      parameters.plus("addAttributes", false),
                                                      env).translation());
    }

    QueryTranslation q = null;
    if (insert.columns() != null && !insert.columns().isEmpty()) {
      st.append(" returning ");
      q = insert.constructResult(st, target(), path, null, parameters, env);
    }

    if (q == null) {
      return new QueryTranslation(insert, st.toString(), emptyList(), emptyMap());
    } else {
      return new QueryTranslation(insert,
                                  st.toString(),
                                  q.columns(),
                                  q.resultAttributes());
    }
  }
}
