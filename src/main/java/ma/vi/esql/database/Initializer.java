package ma.vi.esql.database;

/**
 * Initializers are launched when the application starts and can initialise any
 * necessary database structures.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Initializer {
  void init(Database db);

  /**
   * The special configuration key which indicates that the context is in overwrite
   * mode; in this mode, the system is allowed to overwrite data during the
   * configuration phase of application launch (e.g. overwrite the password of
   * certain special user account). If absent in the configuration, overwrite of
   * existing data should be prohibited to prevent accidental or malicious changes
   * to system data.
   */
  String OVERWRITE = "$overwrite$";

  /**
   * configuration key to provide a source file to the initializer.
   */
  String SOURCE_FILE = "SOURCE_FILE";
}
