package ma.vi.esql.translation;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.query.QueryTranslation;
import ma.vi.esql.syntax.query.Select;
import org.pcollections.PMap;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractTranslator implements Translator {
  @SuppressWarnings("unchecked")
  @Override
  public <T> T translate(Esql<?, T>           esql,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    if      (esql instanceof Select) return (T)translate((Select)esql, esqlCon, path, parameters, env);
    else if (esql instanceof Update) return (T)translate((Update)esql, esqlCon, path, parameters, env);
    else if (esql instanceof Delete) return (T)translate((Delete)esql, esqlCon, path, parameters, env);
    else if (esql instanceof Insert) return (T)translate((Insert)esql, esqlCon, path, parameters, env);
    else                             throw new TranslationException(esql, "Translation of " + esql + " to " + target() + " is not supported");
  }

  protected abstract QueryTranslation translate(Select select, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env);
  protected abstract QueryTranslation translate(Update update, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env);
  protected abstract QueryTranslation translate(Delete delete, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env);
  protected abstract QueryTranslation translate(Insert insert, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env);
}
