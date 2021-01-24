package ma.vi.esql.translator;

import ma.vi.esql.parser.Esql;
import ma.vi.esql.parser.Translatable;

import java.util.Map;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Translator {
  Translatable.Target target();

  <R> R translate(Esql<?, R> esql, Map<String, Object> parameters);
}
