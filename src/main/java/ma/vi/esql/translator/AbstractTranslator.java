package ma.vi.esql.translator;

import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.modify.Insert;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.QueryTranslation;
import ma.vi.esql.parser.query.Select;

import java.util.Map;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractTranslator implements Translator {
  @Override
  public <R> R translate(Esql<?, R> esql, Map<String, Object> parameters) {
    if (esql instanceof Select) {
      return (R)translate((Select)esql, parameters);

    } else if (esql instanceof Update) {
      return (R)translate((Update)esql, parameters);

    } else if (esql instanceof Delete) {
      return (R)translate((Delete)esql, parameters);

    } else if (esql instanceof Insert) {
      return (R)translate((Insert)esql, parameters);

    } else {
      throw new TranslationException("Translation of " + esql + " to " + target() + " is not supported");
    }
  }

  protected abstract QueryTranslation translate(Select select, Map<String, Object> parameters);
  protected abstract QueryTranslation translate(Update update, Map<String, Object> parameters);
  protected abstract QueryTranslation translate(Delete delete, Map<String, Object> parameters);
  protected abstract QueryTranslation translate(Insert insert, Map<String, Object> parameters);
}
