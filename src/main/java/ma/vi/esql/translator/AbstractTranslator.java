package ma.vi.esql.translator;

import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.TranslationException;
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.query.QueryTranslation;
import ma.vi.esql.syntax.query.Select;

import java.util.Map;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractTranslator implements Translator {
  @Override
  public <R> R translate(Esql<?, R> esql, EsqlPath path, Map<String, Object> parameters) {
    if (esql instanceof Select) {
      return (R)translate((Select)esql, path, parameters);

    } else if (esql instanceof Update) {
      return (R)translate((Update)esql, path, parameters);

    } else if (esql instanceof Delete) {
      return (R)translate((Delete)esql, path, parameters);

    } else if (esql instanceof Insert) {
      return (R)translate((Insert)esql, path, parameters);

    } else {
      throw new TranslationException("Translation of " + esql + " to " + target() + " is not supported");
    }
  }

  protected abstract QueryTranslation translate(Select select, EsqlPath path, Map<String, Object> parameters);
  protected abstract QueryTranslation translate(Update update, EsqlPath path, Map<String, Object> parameters);
  protected abstract QueryTranslation translate(Delete delete, EsqlPath path, Map<String, Object> parameters);
  protected abstract QueryTranslation translate(Insert insert, EsqlPath path, Map<String, Object> parameters);
}
