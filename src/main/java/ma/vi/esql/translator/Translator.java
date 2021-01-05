package ma.vi.esql.translator;

import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Translatable;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Translator {
  Translatable.Target target();

  <R> R translate(Esql<?, R> esql);
}