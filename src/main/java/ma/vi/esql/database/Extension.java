package ma.vi.esql.database;

/**
 * An extension is a function invoked on initialisation of the database when it
 * can create tables, functions, install esql transformers, etc. to add features
 * to the base ESQL language.
 *
 * For example extensions can create tables for lookup and create macro/function
 * for resolving lookup values; they can create audit trails tables and set up
 * transformers for intercepting ESQL modifiers (insert/update/delete) and
 * automatically create the necessary audit trails.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Extension {
  void init(Database db, Structure structure);
}
