package ma.vi.esql.translator;

import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.modify.Delete;
import ma.vi.esql.parser.modify.Update;
import ma.vi.esql.parser.query.QueryTranslation;
import ma.vi.esql.parser.query.Select;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class AbstractTranslator implements Translator {
  @Override
  public <R> R translate(Esql<?, R> esql) {
    if (esql instanceof Select) {
      return (R)translate((Select)esql);

    } else if (esql instanceof Update) {
      return (R)translate((Update)esql);

    } else if (esql instanceof Delete) {
      return (R)translate((Delete)esql);

    } else {
      throw new TranslationException("Translation of " + esql + " to " + target() + " is not supported");
    }
  }

  protected abstract QueryTranslation translate(Select select);
  protected abstract QueryTranslation translate(Update update);
  protected abstract QueryTranslation translate(Delete delete);
}
