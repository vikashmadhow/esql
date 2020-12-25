package ma.vi.esql.translator;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.AbstractJoinTableExpr;
import ma.vi.esql.parser.query.QueryTranslation;
import ma.vi.esql.parser.query.SingleTableExpr;
import ma.vi.esql.parser.query.TableExpr;
import ma.vi.esql.type.Type;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class MariaDbTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.MARIADB;
  }

  @Override
  protected QueryTranslation translate(Update update) {
    StringBuilder st = new StringBuilder("update ");

    TableExpr from = update.tables();
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

  @Override
  protected QueryTranslation translate(Delete delete) {
    StringBuilder st = new StringBuilder("delete ");
    QueryTranslation q = null;

    TableExpr from = delete.tables();
    if (from instanceof SingleTableExpr) {
      st.append(" from ").append(from.translate(target()));

    } else if (from instanceof AbstractJoinTableExpr) {
      /*
       * Maria DB allows a form of delete using joins which is close to ESQL
       * but uses the table name instead of the alias.
       */
      SingleTableExpr deleteTable = Delete.findSingleTable((AbstractJoinTableExpr)from, delete.deleteTableAlias());
      if (deleteTable == null) {
        throw new TranslationException("Could not find table with alias " + delete.deleteTableAlias());
      }
      st.append(Type.dbName(deleteTable.tableName(), target()));
      st.append(" from ").append(from.translate(target()));

    } else {
      throw new TranslationException("Wrong table type to delete: " + from);
    }

    if (delete.where() != null) {
      st.append(" where ").append(delete.where().translate(target()));
    }
    if (delete.columns() != null) {
      st.append(" returning ");
      q = delete.constructResult(st, target(), null, true, true);
    }
    if (q == null) {
      return new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                  emptyList(), emptyMap());
    } else {
      return new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                  q.resultAttributeIndices, q.resultAttributes);
    }
  }
}
