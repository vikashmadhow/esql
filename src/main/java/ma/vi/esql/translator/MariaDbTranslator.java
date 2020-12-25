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
public class MariaDbTranslator implements Translator {
  @Override
  public Translatable.Target target() {
    return Translatable.Target.MARIADB;
  }

  @Override
  public <R> R translate(Esql<?, R> esql) {
    if (esql instanceof Update) {
      Update update = (Update)esql;
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
      return (R)new QueryTranslation(st.toString(), emptyList(), emptyMap(),
                                     emptyList(), emptyMap());
    }
    throw new TranslationException("Does not know how to translate " + esql + " to " + target());
  }
}
