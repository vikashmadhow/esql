package ma.vi.esql.translator;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.define.Attribute;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;
import static ma.vi.esql.parser.Translatable.Target.ESQL;
import static ma.vi.esql.parser.modify.Delete.findSingleTable;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EsqlTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return ESQL;
  }

  @Override
  protected QueryTranslation translate(Select select, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("select ");
    if (select.distinct()) {
      st.append("distinct ");
      List<Expression<?>> distinctOn = select.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        st.append("on (")
          .append(distinctOn.stream().map(e -> e.translate(target(), parameters)).collect(joining(", ")))
          .append(") ");
      }
    }

    if (select.explicit() != null && select.explicit()) {
      st.append("explicit ");
    }

    st.append(select.columns().stream()
                    .map(c -> c.translate(target(), parameters))
                    .collect(joining(", ")));

    if (select.tables() != null) {
      st.append(" from ").append(select.tables().translate(target(), parameters));
    }
    if (select.where() != null) {
      st.append(" where ").append(select.where().translate(target(), parameters));
    }
    if (select.groupBy() != null) {
      st.append(select.groupBy().translate(target(), parameters));
    }
    if (select.having() != null) {
      st.append(" having ").append(select.having().translate(target(), parameters));
    }
    if (select.orderBy() != null && !select.orderBy().isEmpty()) {
      st.append(" order by ")
        .append(select.orderBy().stream()
                         .map(e -> e.translate(target(), parameters))
                         .collect(joining(", ")));
    }
    if (select.offset() != null) {
      st.append(" offset ").append(select.offset().translate(target(), parameters));
    }
    if (select.limit() != null) {
      st.append(" limit ").append(select.limit().translate(target(), parameters));
    }
    return new QueryTranslation(st.toString(), null, null, null, null);

  }

  @Override
  protected QueryTranslation translate(Update update, Map<String, Object> parameters) {
    TableExpr from = update.tables();
    if (from instanceof SingleTableExpr) {
      StringBuilder st = new StringBuilder("update ");
      st.append(from.translate(target(), parameters));
      Update.addSet(st, update.set(), target(), false);
      if (update.where() != null) {
        st.append(" where ").append(update.where().translate(target(), parameters));
      }
      QueryTranslation q = null;
      if (update.columns() != null) {
        st.append(" returning ");
        q = update.constructResult(st, target(), null, true, true);
      }
      if (q == null) {
        return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                    emptyList(), emptyMap());
      } else {
        return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                    q.resultAttributeIndices, q.resultAttributes);
      }
    } else if (from instanceof AbstractJoinTableExpr) {
      /*
       * For postgresql the single table referred by the update table alias
       * must be extracted from the table expression and added as the main
       * table of the update statement; the rest of the table expression can
       * then be added to the `from` clause of the update; any join condition
       * removed when extracting the single update table must be moved to the
       * `where` clause. However, the extracted single table cannot then be
       * part of a join condition, which makes the rewriting of the query much
       * more complicated.
       * 
       * A simple approach is to change the update into select and execute it
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
                                    + IntStream.rangeClosed(1, update.set().attributes().size())
                                               .boxed()
                                               .map(i -> "v" + i)
                                               .collect(Collectors.joining(", "))
                                    + ") as (");

      List<Attribute> set = new ArrayList<>(update.set().attributes().values());
      st.append("select \"").append(update.updateTableAlias()).append("\".ctid, ")
        .append(set.stream()
                   .map(a -> a.attributeValue().translate(target(), parameters))
                   .collect(Collectors.joining(", ")))
        .append(" from ").append(from.translate(target(), parameters));

      if (update.where() != null) {
        st.append(" where ").append(update.where().translate(target(), parameters));
      }
      st.append(") update ");

      SingleTableExpr updateTable = findSingleTable((AbstractJoinTableExpr)from,
                                                    update.updateTableAlias());
      if (updateTable == null) {
        throw new TranslationException("Could not find table with alias " + update.updateTableAlias());
      }
      st.append(updateTable.translate(target(), parameters));

      st.append(" set ");
      boolean first = true;
      for (int i = 0; i < set.size(); i++) {
        if (first) { first = false;   }
        else       { st.append(", "); }
        Attribute a = set.get(i);
        st.append(a.name()).append('=').append(withAlias).append(".v").append(i+1);
      }
      st.append(" from ").append(withAlias)
        .append(" where \"").append(update.updateTableAlias())
        .append("\".ctid=").append(withAlias).append(".id");

      QueryTranslation q = null;
      if (update.columns() != null) {
        st.append(" returning ");
        q = update.constructResult(st, target(), null, true, true);
      }
      if (q == null) {
        return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                    emptyList(), emptyMap());
      } else {
        return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                    q.resultAttributeIndices, q.resultAttributes);
      }
    } else {
      throw new TranslationException("Wrong table type to update: " + from);
    }
  }

  @Override
  protected QueryTranslation translate(Delete delete, Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("delete ");

    TableExpr from = delete.tables();
    if (from instanceof SingleTableExpr) {
      st.append(" from ").append(from.translate(target(), parameters));

    } else if (from instanceof AbstractJoinTableExpr) {
      /*
       * For postgresql the single table referred by the delete table alias
       * must be extracted from the table expression and added as the main
       * table of the update statement; the rest of the table expression can
       * then be added to the `using` clause of the delete; any join condition
       * removed when extracting the single update table must be moved to the
       * `where` clause.
       */
      T2<AbstractJoinTableExpr, SingleTableExpr> deleteTable =
          Update.removeSingleTable((AbstractJoinTableExpr)from,
                                   delete.deleteTableAlias());
      if (deleteTable == null) {
        throw new TranslationException("Could not find table with alias " + delete.deleteTableAlias());
      }
      if (deleteTable.a instanceof JoinTableExpr) {
        JoinTableExpr join = (JoinTableExpr)deleteTable.a;
        delete.where(join.on(), false);
      }
      st.append(" from ").append(deleteTable.b.translate(target(), parameters));
      st.append(" using ").append(from.translate(target(), parameters));

    } else {
      throw new TranslationException("Wrong table type to delete: " + from);
    }

    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target(), parameters));
    }

    if (delete.columns() != null) {
      st.append(" returning ");
      QueryTranslation q = delete.constructResult(st, target(), null, true, true);
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                  q.resultAttributeIndices, q.resultAttributes);
    } else {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                  emptyList(), emptyMap());
    }
  }
}
