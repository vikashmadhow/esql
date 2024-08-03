package ma.vi.esql.translation;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.modify.Delete;
import ma.vi.esql.syntax.modify.Insert;
import ma.vi.esql.syntax.modify.Update;
import ma.vi.esql.syntax.query.QueryTranslation;
import ma.vi.esql.syntax.query.QueryUpdate;
import ma.vi.esql.syntax.query.Select;
import org.pcollections.PMap;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

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
    T trans = switch(esql) {
      case Select s -> (T)translate(s, esqlCon, path, parameters, env);
      case Update s -> (T)translate(s, esqlCon, path, parameters, env);
      case Delete s -> (T)translate(s, esqlCon, path, parameters, env);
      case Insert s -> (T)translate(s, esqlCon, path, parameters, env);
      default       -> throw new TranslationException(esql, "Translation of " + esql + " to " + target() + " is not supported");
    };
//    T trans = esql instanceof Select s ? (T)translate(s, esqlCon, path, parameters, env)
//            : esql instanceof Update u ? (T)translate(u, esqlCon, path, parameters, env)
//            : esql instanceof Delete d ? (T)translate(d, esqlCon, path, parameters, env)
//            : esql instanceof Insert i ? (T)translate(i, esqlCon, path, parameters, env)
//            : null;
//    if (trans == null)
//      throw new TranslationException(esql, "Translation of " + esql + " to " + target() + " is not supported");

    if (target() == Translatable.Target.JAVASCRIPT) {
      String jsTrans = "(await $exec.select(`" + String.valueOf(trans) + "`))";
      return (T)new QueryTranslation((QueryUpdate)esql,
                                     jsTrans,
                                     emptyList(),
                                     emptyMap());
    }
    return trans;

//    if      (esql instanceof Select) return (T)translate((Select)esql, esqlCon, path, parameters, env);
//    else if (esql instanceof Update) return (T)translate((Update)esql, esqlCon, path, parameters, env);
//    else if (esql instanceof Delete) return (T)translate((Delete)esql, esqlCon, path, parameters, env);
//    else if (esql instanceof Insert) return (T)translate((Insert)esql, esqlCon, path, parameters, env);
//    else                             throw new TranslationException(esql, "Translation of " + esql + " to " + target() + " is not supported");
  }

  protected abstract QueryTranslation translate(Select select, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env);
  protected abstract QueryTranslation translate(Update update, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env);
  protected abstract QueryTranslation translate(Delete delete, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env);
  protected abstract QueryTranslation translate(Insert insert, EsqlConnection esqlCon, EsqlPath path, PMap<String, Object> parameters, Environment env);
}
