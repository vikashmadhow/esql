package ma.vi.esql.translator;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.QueryTranslation;
import ma.vi.esql.parser.query.SingleTableExpr;
import ma.vi.esql.parser.query.TableExpr;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class HSqlDbTranslator extends AbstractTranslator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.HSQLDB;
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
