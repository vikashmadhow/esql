package ma.vi.esql.translator;

import ma.vi.base.tuple.T2;
import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class PostgresqlTranslator implements Translator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.POSTGRESQL;
  }

  @Override
  public <R> R translate(Esql<?, R> esql) {
    if (esql instanceof Update) {
      Update update = (Update)esql;
      StringBuilder st = new StringBuilder("update ");
      QueryTranslation q = null;

      TableExpr from = update.tables();
      if (from instanceof SingleTableExpr) {
        st.append(from.translate(target()));

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
        return (R)new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                       emptyList(), emptyMap());
      } else {
        return (R)new QueryTranslation(st.toString(), q.columns, q.columnToIndex,
                                       q.resultAttributeIndices, q.resultAttributes);
      }
    }
    throw new TranslationException("Translation of " + esql + " to " + target() + " is not supported");
  }
}
