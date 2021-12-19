package ma.vi.esql.translator;

import ma.vi.base.tuple.T1;
import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.TranslationException;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.syntax.modify.InsertRow;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;
import static ma.vi.esql.syntax.modify.Delete.findSingleTable;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class PostgresqlTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.POSTGRESQL;
  }

  @Override
  protected QueryTranslation translate(Select select,
                                       EsqlPath path,
                                       Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("select ");
    if (select.distinct()) {
      st.append("distinct ");
      List<Expression<?, String>> distinctOn = select.distinctOn();
      if (distinctOn != null && !distinctOn.isEmpty()) {
        st.append("on (")
          .append(distinctOn.stream().map(e -> e.translate(target(), path.add(e), parameters)).collect(joining(", ")))
          .append(") ");
      }
    }

    // add output clause
    QueryTranslation q = select.constructResult(st, target(), path, null, parameters);
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
    TableExpr from = update.tables();
    if (from instanceof SingleTableExpr) {
      StringBuilder st = new StringBuilder("update ");
      st.append(from.translate(target(), path.add(from), parameters));
      Update.addSet(st, update.set(), target(), false, path);
      if (update.where() != null) {
        st.append(" where ").append(update.where().translate(target(), path.add(update.where()), parameters));
      }
      QueryTranslation q = null;
      if (update.columns() != null && !update.columns().isEmpty()) {
        st.append(" returning ");
        q = update.constructResult(st, target(), path, null, parameters);
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
                                    + IntStream.rangeClosed(1, update.set().attributes().size())
                                               .boxed()
                                               .map(i -> "v" + i)
                                               .collect(joining(", "))
                                    + ") as (");

      List<Attribute> set = new ArrayList<>(update.set().attributes().values());
      st.append("select \"").append(update.updateTableAlias()).append("\".ctid, ")
        .append(set.stream()
                   .map(a -> a.attributeValue().translate(target(), path.add(a.attributeValue()), parameters))
                   .collect(joining(", ")))
        .append(" from ").append(from.translate(target(), path.add(from), parameters));

      if (update.where() != null) {
        st.append(" where ").append(update.where().translate(target(), path.add(update.where()), parameters));
      }
      st.append(") update ");

      SingleTableExpr updateTable = findSingleTable((AbstractJoinTableExpr)from,
                                                    update.updateTableAlias());
      if (updateTable == null) {
        throw new TranslationException("Could not find table with alias " + update.updateTableAlias());
      }
      st.append(updateTable.translate(target(), path.add(updateTable), parameters));

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
        q = update.constructResult(st, target(), path, null, parameters);
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
  protected QueryTranslation translate(Delete delete,
                                       EsqlPath path,
                                       Map<String, Object> parameters) {
    StringBuilder st = new StringBuilder("delete ");

    TableExpr from = delete.tables();
    if (from instanceof SingleTableExpr) {
      st.append(" from ").append(from.translate(target(), path.add(from), parameters));

    } else if (from instanceof AbstractJoinTableExpr) {
      /*
       * For postgresql the single table referred by the delete table alias must
       * be extracted from the table expression and added as the main table of
       * the update statement; the rest of the table expression can then be added
       * to the `using` clause of the delete; any join condition removed when
       * extracting the single update table must be moved to the `where` clause.
       */
//      T2<AbstractJoinTableExpr, SingleTableExpr> deleteTable =
//          removeSingleTable((AbstractJoinTableExpr)from,
//                            delete.deleteTableAlias());

      T1<AbstractJoinTableExpr> deleteJoin = new T1<>();
      T1<SingleTableExpr> deleteTable = new T1<>();
      String singleTableAlias = delete.deleteTableAlias();
      delete = delete.map((e, p) -> {
        if (e instanceof AbstractJoinTableExpr join) {
          if (join.left() instanceof SingleTableExpr table
           && singleTableAlias.equals(table.alias())) {
            /*
             *        J                    J
             *      /  \    remove x     /  \
             *     J    z  ---------->  y    z
             *    / \
             *   x  y
             */
            deleteJoin.a = join;
            deleteTable.a = table;
            return join.right();

          } else if (join.right() instanceof SingleTableExpr table
                  && singleTableAlias.equals(table.alias())) {
            /*
             *        J                     J
             *      /  \   remove y       /  \
             *     J    z ---------->    x    z
             *    / \
             *   x  y
             */
            deleteJoin.a = join;
            deleteTable.a = table;
            return join.left();
          }
        }
        return e;
      }, null, path.add(delete));

      if (deleteTable.a == null) {
        throw new TranslationException("Could not find table with alias " + singleTableAlias);
      }
      if (deleteJoin.a instanceof JoinTableExpr join) {
        delete = delete.where(join.on(), false);
      }
      st.append(" from ").append(deleteTable.a.translate(target(), path.add(deleteTable.a), parameters));
      st.append(" using ").append(from.translate(target(), path.add(from), parameters));

    } else {
      throw new TranslationException("Wrong table type to delete: " + from);
    }

    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target(), path.add(delete.where()), parameters));
    }

    if (delete.columns() != null && !delete.columns().isEmpty()) {
      st.append(" returning ");
      QueryTranslation q = delete.constructResult(st, target(), path, null, parameters);
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                  q.resultAttributeIndices, q.resultAttributes);
    } else {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                  emptyList(), emptyMap());
    }
  }


//  /**
//   * For postgresql updates, the table to be updated must be specified after the
//   * `update` keyword and not repeated in the from table expression (unless for
//   * a self-join). Since ESQL allows a single table expression containing the
//   * whole join together with the table to update, the latter must be extracted.
//   * This method finds the table to update (using its alias supplied in the
//   * update statement), removes it from the whole table expression and returns
//   * it, together with the parent join it belongs to. The parent join is used to
//   * extract any condition set on the join and put it in the `where` clause.
//   *
//   * @param join The join to search for single update table.
//   * @param singleTableAlias The alias of the single table being searched.
//   * @return The single table to update together with the parent join it belongs
//   *         to. The parent join is used to extract any condition set on the
//   *         join and put it in the `where` clause of the query.
//   */
//  private static T2<AbstractJoinTableExpr, SingleTableExpr> removeSingleTable(AbstractJoinTableExpr join,
//                                                                              String singleTableAlias) {
//    if (join.left() instanceof SingleTableExpr table
//     && singleTableAlias.equals(((SingleTableExpr)join.left()).alias())) {
//      /*
//       *        J                    J
//       *      /  \    remove x     /  \
//       *     J    z  ---------->  y    z
//       *    / \
//       *   x  y
//       */
//      join.replaceWith(join.right());
//      return T2.of(join, table);
//
//    } else if (join.right() instanceof SingleTableExpr table
//            && singleTableAlias.equals(((SingleTableExpr)join.right()).alias())) {
//      /*
//       *        J                     J
//       *      /  \   remove y       /  \
//       *     J    z ---------->    x    z
//       *    / \
//       *   x  y
//       */
//
//      join.replaceWith(join.left());
//      return T2.of(join, table);
//
//    } else if (join.left() instanceof AbstractJoinTableExpr) {
//      return removeSingleTable((AbstractJoinTableExpr)join.left(), singleTableAlias);
//
//    } else if (join.right() instanceof AbstractJoinTableExpr) {
//      return removeSingleTable((AbstractJoinTableExpr)join.right(), singleTableAlias);
//
//    } else {
//      return null;
//    }
//  }

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
      st.append(' ').append(insert.select().translate(target(),
                                                      path.add(insert.select()),
                                                      Map.of("addAttributes", false)).statement);
    }

    QueryTranslation q = null;
    if (insert.columns() != null && !insert.columns().isEmpty()) {
      st.append(" returning ");
      q = insert.constructResult(st, target(), path, null, parameters);
    }

    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(), emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                  q.resultAttributeIndices, q.resultAttributes);
    }
  }
}
