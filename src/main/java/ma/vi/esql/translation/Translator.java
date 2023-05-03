package ma.vi.esql.translation;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.modify.UpdateSet;
import org.pcollections.PMap;

import static ma.vi.esql.translation.Translatable.Target;
import static ma.vi.esql.translation.Translatable.Target.ESQL;

/**
 * <p>
 * A translator knows how to translate (some) ESQL nodes to certain targets.
 * Translators are useful to separate translation logic by targets (instead of
 * combining all the targets into the single translate function and using `if`s
 * to select the specific target branch) and, secondarily, combine related
 * translation logic in a single class; for instance, translation logic for
 * `select`, `update`, `delete` and `insert` statements have certain commonalities
 * which can be easier to maintain if gathered in a single translator class for
 * a specific target.
 * </p>
 *
 * <p>
 * The existing translation mechanism consists of every ESQL node implementing
 * the {@link Translatable#translate(Translatable.Target)} method of the
 * {@link Translatable} interface, which is called to produce the translation for
 * that node. This has been augmented with the Esql#trans(Translatable.Target)
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
  Target target();

  /**
   * Call to translate an Esql node.
   */
  <R> R translate(Esql<?, R>            esql,
                  EsqlConnection        esqlCon,
                  EsqlPath              path,
                  PMap<String, Object>  parameters,
                  Environment           env);

  /**
   * Static translation utility functions.
   */
  class Util {
    /**
     * Produces the set clause of an update statement for a specific target. This
     * is here are it is almost the same for all targets.
     */
    public static void addSet(StringBuilder st,
                              UpdateSet     updateSet,
                              Target        target,
                              boolean       removeQualifier,
                              EsqlPath      path,
                              Environment   env) {
      boolean first = true;
      st.append(" set ");
      for (Attribute set: updateSet.sets().values()) {
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
        Expression<?, ?> expr = set.attributeValue();
        st.append('=').append(expr.translate(target, null, path.add(expr), env));
      }
    }
  }
}
