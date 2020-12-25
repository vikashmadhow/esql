package ma.vi.esql.translator;

import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.QueryTranslation;
import ma.vi.esql.parser.query.TableExpr;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SqlServerTranslator implements Translator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.SQLSERVER;
  }

  @Override
  public <R> R translate(Esql<?, R> esql) {
    if (esql instanceof Update) {
      Update update = (Update)esql;
      StringBuilder st = new StringBuilder("update ");

      TableExpr from = update.tables();
      st.append(update.updateTableAlias());
      Update.addSet(st, update.set(), target(), true);

      // output clause for SQL Server if specified
      QueryTranslation q = null;
      if (update.columns() != null) {
        st.append(" output ");
        q = update.constructResult(st, target(), "inserted", true, true);
      }
      st.append(" from ").append(from.translate(target()));

      if (update.where() != null) {
        st.append(" where ").append(update.where().translate(target()));
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
