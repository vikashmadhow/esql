package ma.vi.esql.translator;

import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.Metadata;

import java.util.Map;

import static ma.vi.esql.syntax.Translatable.Target.ESQL;

/**
 * <p>
 * A translator knows how to translate (some) ESQL nodes to certain targets.
 * Translators are useful to combine related translation logic in a single class;
 * for instance, translation logic for `select`, `update`, `delete` and `insert`
 * statements have certain commonalities which can be easier to maintain if gathered
 * in a single translator class.
 * </p>
 *
 * <p>
 * The existing translation mechanism consists of every ESQL node implementing
 * the {@link Translatable#translate(Translatable.Target)} method of the
 * {@link Translatable} interface, which is called to produce the translation for
 * that node. This has been augmented with the {@link Esql#trans(Translatable.Target)}
 * method which has the same signature as the translate method. The implementation
 * of the `translate` method in the `Esql` class now delegates to `trans` which,
 * by default, looks for translator implementation for the target (using {@link TranslatorFactory}
 * and delegates to the latter. The trans method is then overriden by `Esql`
 * subclasses which produce their own translation (i.e., using the existing mechanism).
 * Thus both translation through a Translator and translation by each ESQL node
 * are supported through the overriding of the trans method (or leaving it to the
 * implementation in the parent `Esql`).
 * </p>
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Translator {
  /**
   * The target supported by this translator.
   */
  Translatable.Target target();

  /**
   * Call to translate an Esql node.
   */
  <R> R translate(Esql<?, R> esql, EsqlPath path, Map<String, Object> parameters);

  /**
   * Static translation utility functions.
   */
  class Util {
    /**
     * Produces the set clause of an update statement for a specific target. This
     * is here are it is almost the same for all targets.
     */
    public static void addSet(StringBuilder st,
                              Metadata sets,
                              Translatable.Target target,
                              boolean removeQualifier,
                              EsqlPath path) {
      boolean first = true;
      st.append(" set ");
      for (Attribute set: sets.attributes().values()) {
        if (first) {
          first = false;
        } else {
          st.append(", ");
        }
        String columnName = set.name();
        if (removeQualifier) {
          int pos = columnName.lastIndexOf('.');
          if (pos != -1) {
            columnName = columnName.substring(pos + 1);
          }
        }
        if (target != ESQL) st.append('"');
        st.append(columnName);
        if (target != ESQL) st.append('"');
        st.append('=').append(set.attributeValue().translate(target, path));
      }
    }
  }
}
