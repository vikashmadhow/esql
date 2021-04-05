package ma.vi.esql.database;

import java.util.Set;

/**
 * An extension is a function invoked on initialisation of the database whereupon
 * it can create tables, functions, install esql transformers, etc. to add features
 * to the base ESQL language.
 *
 * For example, extensions can create tables for lookup and create macro/function
 * for resolving lookup values; they can create audit trails tables and set up
 * transformers for intercepting ESQL modifiers (insert/update/delete) and
 * automatically create the necessary audit trails; they can be used
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Extension {

  void init(Database db, Structure structure);

  Set<Class<? extends Extension>> dependsOn();
}
