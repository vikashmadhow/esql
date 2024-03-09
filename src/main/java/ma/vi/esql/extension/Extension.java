package ma.vi.esql.extension;

import ma.vi.base.config.Configuration;
import ma.vi.esql.database.Database;

import java.util.Collections;
import java.util.Map;

/**
 * An extension is invoked on initialisation of the database whereupon it can
 * create tables, functions, install esql transformers, etc. to add features
 * to the base ESQL language.
 *
 * For example, extensions can create tables for lookup and create macro/function
 * for resolving lookup values; they can create audit trails tables and set up
 * transformers for intercepting ESQL modifiers (insert/update/delete) and
 * automatically create the necessary audit trails; they can be used to simulate
 * hierarchical (parent-child) relations in tables, and so on.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Extension {
  /**
   * Initialises the database with tables, functions, macros, transformers, etc.
   * required to implement this extension.
   */
  void init(Database db, Configuration parameters);

  default void init(Database db) {
    init(db, Configuration.EMPTY);
  }

  /**
   * This is called after initialisation and can contain code that requires the
   * extension to have been installed. E.g., an initializer for reports that
   * requires the report extension to be available.
   */
  default void postInit(Database db, Configuration parameters) {}

  /**
   * Returns a set of extension classes that must be loaded and initialised
   * before this extension is initialized. If this extension does not depend on
   * any other extensions, this method should return null or an empty set.
   */
  default Map<Class<? extends Extension>, Configuration> dependsOn() {
    return Collections.emptyMap();
  }
}
