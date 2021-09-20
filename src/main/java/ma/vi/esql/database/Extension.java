package ma.vi.esql.database;

import java.util.Set;

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
  void init(Database db);

  /**
   * Returns a set of extension classes that must be loaded and initialised
   * before this extension is initialized. If this extension does not depend on
   * any other extensions, this method should return null or an empty set.
   */
  Set<Class<? extends Extension>> dependsOn();
}
