package ma.vi.esql.translator;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;


/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class PostgresqlTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.POSTGRESQL;
  }

  @Override
  protected QueryTranslation translate(Update update) {
    StringBuilder st = new StringBuilder("update ");
    QueryTranslation q = null;

    TableExpr from = update.tables();
    if (from instanceof SingleTableExpr) {
      st.append(from.translate(target()));
      Update.addSet(st, update.set(), target(), false);

    } else if (from instanceof AbstractJoinTableExpr) {
      /*
       * For postgresql the single table referred by the update table alias
       * must be extracted from the table expression and added as the main
       * table of the update statement; the rest of the table expression can
       * then be added to the `from` clause of the update; any join condition
       * removed when extracting the single update table must be moved to the
       * `where` clause.
       */
      T2<AbstractJoinTableExpr, SingleTableExpr> updateTable =
          Update.removeSingleTable((AbstractJoinTableExpr)from,
                                   update.updateTableAlias());
      if (updateTable == null) {
        throw new TranslationException("Could not find table with alias " + update.updateTableAlias());
      }
      if (updateTable.a instanceof JoinTableExpr) {
        JoinTableExpr join = (JoinTableExpr)updateTable.a;
        update.where(join.on(), false);
      }
      st.append(updateTable.b.translate(target()));
      Update.addSet(st, update.set(), target(), false);
      st.append(" from ").append(update.tables().translate(target()));

    } else {
      throw new TranslationException("Wrong table type to update: " + from);
    }

    if (update.where() != null) {
      st.append(" where ").append(update.where().translate(target()));
    }
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
  }

  @Override
  protected QueryTranslation translate(Delete delete) {
    StringBuilder st = new StringBuilder("delete ");

    TableExpr from = delete.tables();
    if (from instanceof SingleTableExpr) {
      st.append(" from ").append(from.translate(target()));
//      st.append(" from ")
//        .append(Type.dbName(((SingleTableExpr)from).tableName(), target()));

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
      st.append(" from ").append(deleteTable.b.translate(target()));
      st.append(" using ").append(from.translate(target()));

    } else {
      throw new TranslationException("Wrong table type to delete: " + from);
    }

    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target()));
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
