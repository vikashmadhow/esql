package ma.vi.esql.translator;

import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;

import java.util.Map;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Translator {
  Translatable.Target target();

  <R> R translate(Esql<?, R> esql, EsqlPath path, Map<String, Object> parameters);
}
