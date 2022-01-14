package ma.vi.esql.translation;

import ma.vi.base.lang.NotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class TranslatorFactory {
  public static void register(Translatable.Target target, Translator translator) {
    translators.put(target, translator);
  }

  public static Translator get(Translatable.Target target) {
    if (!translators.containsKey(target)) {
      throw new NotFoundException("No translator for " + target + " registered");
    }
    return translators.get(target);
  }

  private static final Map<Translatable.Target, Translator> translators = new ConcurrentHashMap<>();
}
