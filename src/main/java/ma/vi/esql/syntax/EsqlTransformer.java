package ma.vi.esql.syntax;

import ma.vi.esql.database.Database;

/**
 * <p>
 * An ESQL transformer can transform an ESQL statement into a different ESQL
 * statement. They are registered with the system and are invoked in the order
 * of registration before an ESQL statement is executed.
 * </p>
 *
 * <p>
 * They are invoked after parameter substitution and macro expansion of the ESQL
 * statement and can be viewed as an external macro expansion system. After all
 * transformers have been run, the final ESQL returned is translated and executed
 * on the database.
 * </p>
 *
 * <p>
 * Transformers can be used to implement logic for access control, auditing,
 * optimization, etc.
 * </p>
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface EsqlTransformer {
  Esql<?, ?> transform(Database db, Esql<?, ?> esql);
}
