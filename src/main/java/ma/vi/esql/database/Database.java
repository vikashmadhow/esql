/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.EsqlConnectionImpl;
import ma.vi.esql.parser.EsqlTransformer;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.define.ConstraintDefinition;
import ma.vi.esql.parser.define.Metadata;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.type.BaseRelation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a database containing tables and other persistent objects
 * which can be accessed through the {@link Structure} instance obtained
 * from the database. Connections to the database can be similarly obtained
 * from its instance and used to query and modify it.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Database {
  // Initialisation
  ////////////////////////////////////////////////

  /**
   * Initialise the database using the provided configuration map.
   * The following map keys are recognised:
   * <ul>
   *   <li><b>database.host:</b> the database host (default localhost).</li>
   *   <li><b>database.port:</b> if specified, the database port.</li>
   *   <li><b>database.name:</b> name of the database.</li>
   *   <li><b>database.user.name:</b> default username for connecting to the database.</li>
   *   <li><b>database.user.password:</b> default password for connecting to the database.</li>
   *   <li><b>database.createCoreTables:</b> whether or not to create the core tables if missing; default is true.</li>
   *   <li><b>database.extensions:</b> An array of classes implementing the Extension interface
   *                                   which are initialised when the database starts.</li>
   * </ul>
   *
   * This method must be called when the database object is created.
   *
   * @param config Contains the configuration keys for connecting to the database.
   */
  void init(Map<String, Object> config);

  /**
   * Returns an unmodifiable copy of the default config supplied when the database was created.
   */
  Map<String, Object> config();

  /**
   * Returns the structure of the objects (relations) in the database, loading it when first called.
   * This method will be called by {@link #init(Map)}.
   */
  Structure structure();

  /**
   * Whether or not to create the core tables (in which are stored metadata info
   * on all tables and columns, among others), if they are not present. This
   * method returns the value of the configuration parameter
   * `database.createCoreTables` if it is present, or true otherwise.
   */
  default boolean createCoreTables() {
    return (Boolean)config().getOrDefault("database.createCoreTables", Boolean.TRUE);
  }

  /**
   * Performs post initialisation of the database where special objects, such as tables
   * to hold metadata on other tables, can be created in the database. This is called after
   * the {@link #structure()} method which allows the use of ESQL to create the objects.
   */
  void postInit(Connection con, Structure structure);

  /**
   * Returns the translation target for this database.
   */
  Translatable.Target target();

  /**
   * Adds an ESQL transformer to the list of transformers that are used to
   * transform ESQL statements prior to their executions.
   */
  void addEsqlTransformer(EsqlTransformer transformer);

  /**
   * Removes a ESQL transformer from the list of transformers that was used to
   * transform ESQL statements prior to their executions, if it exists. Returns
   * true if the transformer was successfully removed.
   */
  boolean removeEsqlTransformer(EsqlTransformer transformer);

  /**
   * Returns the list of active ESQL transformers used to transform ESQL
   * statements prior to their executions.
   */
  List<EsqlTransformer> esqlTransformers();

  // Connections
  //////////////////////////////////////////////////////
  /**
   * Returns a connection from the pool to the underlying database.
   */
  Connection pooledConnection(boolean autoCommit,
                              int isolationLevel,
                              String username,
                              String password);

  default Connection pooledConnection(boolean autoCommit,
                                      int isolationLevel) {
    return pooledConnection(autoCommit,
                            isolationLevel,
                            String.valueOf(config().get("database.user.name")),
                            String.valueOf(config().get("database.user.password")));
  }

  default Connection pooledConnection() {
    return pooledConnection(false, -1);
  }

  default Connection pooledConnection(String username, String password) {
    return pooledConnection(false, -1, username, password);
  }

  /**
   * Returns a raw connection (unpooled) to the underlying database.
   * Raw connections provide access to database-specific functionalities
   * such as bulk copying through the CopyManager in Postgresql.
   */
  Connection rawConnection(boolean autoCommit,
                           int isolationLevel,
                           String username,
                           String password);

  default Connection rawConnection(boolean autoCommit, int isolationLevel) {
    return rawConnection(autoCommit,
                         isolationLevel,
                         String.valueOf(config().get("database.user.name")),
                         String.valueOf(config().get("database.user.password")));
  }

  default Connection rawConnection() {
    return rawConnection(false, -1);
  }

  default Connection rawConnection(String username, String password) {
    return rawConnection(false, -1, username, password);
  }

  /**
   * Returns a connection through which Esql commands can be executed.
   */
  default EsqlConnection esql(Connection con) {
    return new EsqlConnectionImpl(this, con);
  }

  /**
   * The schema for the core information tables.
   */
  String CORE_SCHEMA = "_core";

  // array support
  ////////////////////////////////

  /**
   * Returns a Java array for the array in column index in the resultset.
   *
   * @param rs            The ResultSet containing the array column.
   * @param index         The index of column with the array value.
   * @param componentType The component type of the array.
   * @param <T>           Component type of the array.
   * @return A Java array corresponding to the underlying SQL array.
   * @throws SQLException If an error occurs when reading the array data from the database.
   */
  <T> T[] getArray(ResultSet rs, String index, Class<T> componentType) throws SQLException;

  /**
   * Exactly as {@link #getArray(ResultSet, String, Class)} but with an
   * integer index for the column containing the array value.
   */
  <T> T[] getArray(ResultSet rs, int index, Class<T> componentType) throws SQLException;

  /**
   * Sets the array as a parameter at the specified index in the
   * prepared statement.
   *
   * @param ps         The prepared statement expecting an array parameter.
   * @param paramIndex The index where the array parameter is expected.
   * @param array      The array to set.
   * @throws SQLException If an error occurs when setting the parameter.
   */
  void setArray(PreparedStatement ps, int paramIndex, Object array) throws SQLException;

  // Update information tables
  ///////////////////////////////////////////////////

  /**
   * Adds a table to the core information tables if latter are present.
   */
  void table(Connection con, BaseRelation table);

  /**
   * Rename the table in the core information tables, if latter are present.
   */
  void tableName(Connection con, UUID tableId, String name);

  void clearTableMetadata(Connection con, UUID tableId);

  void tableMetadata(Connection con, UUID tableId, Metadata metadata);

  void dropTable(Connection con, UUID tableId);

  void column(Connection con, UUID tableId, Column column);

  void columnName(Connection con, UUID columnId, String name);

  void columnType(Connection con, UUID columnId, String type);

  void defaultValue(Connection con, UUID columnId, String defaultValue);

  void notNull(Connection con, UUID columnId, String notNull);

  void columnMetadata(Connection con, UUID columnId, Metadata metadata);

  void dropColumn(Connection con, UUID columnId);

  void constraint(Connection con, UUID tableId, ConstraintDefinition constraint);

  void dropConstraint(Connection con, UUID tableId, String constraintName);
}
