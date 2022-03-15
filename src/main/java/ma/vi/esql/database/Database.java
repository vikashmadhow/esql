/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import ma.vi.base.config.Configuration;
import ma.vi.base.lang.NotFoundException;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.syntax.EsqlTransformer;
import ma.vi.esql.syntax.define.ConstraintDefinition;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.translation.Translatable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
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
   * The database system (SQL_SERVER, POSTGRESQL, ORACLE, etc.).
   */
  String CONFIG_DB_SYSTEM = "database.system";

  String DB_SYSTEM_SQLSERVER  = "SQLSERVER";
  String DB_SYSTEM_POSTGRESQL = "POSTGRESQL";
  String DB_SYSTEM_MYSQL      = "MYSQL";
  String DB_SYSTEM_MARIADB    = "MARIADB";
  String DB_SYSTEM_SQLITE     = "SQLITE";

  /**
   * The database host configuration parameter (default localhost).
   */
  String CONFIG_DB_HOST = "database.host";

  /**
   * The database port configuration parameter. If not specified the database
   * default is used.
   */
  String CONFIG_DB_PORT = "database.port";

  /**
   * Configuration parameter for the name of the database.
   */
  String CONFIG_DB_NAME = "database.name";

  /**
   * Configuration parameter for the username to connect to the database.
   */
  String CONFIG_DB_USER = "database.user.name";

  /**
   * Configuration parameter for the password to connect to the database.
   */
  String CONFIG_DB_PASSWORD = "database.user.password";

  /**
   * Configuration parameter for whether or not to create the core tables if
   * missing; default is true.
   */
  String CONFIG_DB_CREATE_CORE_TABLES = "database.createCoreTables";

  /**
   * Configuration parameter for extensions. The value of this parameter must be
   * a map of classes implementing the {@link Extension} interface, mapped to a
   * set of string parameters (another map of string to string). For example
   *
   *    Map.of(LookupExtension.class, Map.of("schema", "_lookup"),
   *           UserExtension.class,   Map.of("jwt", "true", "two-factor", "false"))
   *
   * Extension classes are initialised when the database starts.
   */
  String CONFIG_DB_EXTENSIONS = "database.extensions";

  /**
   * Initialise the database using the provided configuration.
   * The following map keys are recognised:
   * <ul>
   *   <li><b>database.host:</b> the database host (default localhost).</li>
   *   <li><b>database.port:</b> if specified, the database port.</li>
   *   <li><b>database.name:</b> name of the database.</li>
   *   <li><b>database.user.name:</b> default username for connecting to the database.</li>
   *   <li><b>database.user.password:</b> default password for connecting to the database.</li>
   *   <li><b>database.createCoreTables:</b> whether or not to create the core tables if missing; default is true.</li>
   *   <li><b>database.extensions:</b> A set of classes implementing the Extension interface
   *                                   which are initialised when the database starts.</li>
   * </ul>
   *
   * This method must be called when the database object is created.
   *
   * @param config Contains the configuration keys for connecting to the database.
   */
  void init(Configuration config);

  /**
   * Returns the configuration with which the database was created.
   */
  Configuration config();

  /**
   * Returns the structure of the objects (relations) in the database, loading it when first called.
   * This method will be called by {@link #init(Configuration)}.
   */
  Structure structure();

  /**
   * Whether or not to create the core tables (in which are stored metadata info
   * on all tables and columns, among others), if they are not present. This
   * method returns the value of the configuration parameter
   * `database.createCoreTables` if it is present, or true otherwise.
   */
  default boolean createCoreTables() {
    return config().get(CONFIG_DB_CREATE_CORE_TABLES, Boolean.TRUE);
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

  /**
   * Returns the extension class of type E loaded in the database or throws
   * NotFoundException if no such extension has been loaded.
   */
  <E extends Extension> E extension(Class<? extends Extension> e) throws NotFoundException;

  // Connections
  //////////////////////////////////////////////////////
  /**
   * Returns a connection from the pool to the underlying database.
   */
  Connection pooledConnection(boolean autoCommit,
                              int isolationLevel);

  default Connection pooledConnection() {
    return pooledConnection(false, -1);
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
                         config().get("database.user.name"),
                         config().get("database.user.password"));
  }

  default Connection rawConnection() {
    return rawConnection(false, -1);
  }

  default Connection rawConnection(String username, String password) {
    return rawConnection(false, -1, username, password);
  }

  /**
   * Returns a connection through which Esql commands can be executed. If the
   * @param con The underlying database connection to use. If this is null, the
   *            last created connection for this thread is used, if it is still
   *            opened and valid. Otherwise a new connection is created and set
   *            as the current active one.
   */
  EsqlConnection esql(Connection con);

  default EsqlConnection esql() {
    return esql(null);
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
  void addTable(EsqlConnection con, BaseRelation table);

  /**
   * Returns the table id of the table with the specified name in the core
   * information table if it exists, or returns null otherwise.
   */
  UUID tableId(EsqlConnection con, String tableName);

  /**
   * Updates the table data in the core information tables; returns a new
   * BaseRelation table representing the updated table.
   */
  BaseRelation updateTable(EsqlConnection con, BaseRelation table);

  /**
   * Rename the table in the core information tables, if latter are present.
   */
  void renameTable(EsqlConnection con, UUID tableId, String name);

  void clearTableMetadata(EsqlConnection con, UUID tableId);

  void tableMetadata(EsqlConnection con, UUID tableId, Metadata metadata);

  void dropTable(EsqlConnection con, UUID tableId);

  void column(EsqlConnection con, UUID tableId, Column column);

  void columnName(EsqlConnection con, UUID columnId, String name);

  void columnType(EsqlConnection con, UUID columnId, String type);

  void defaultValue(EsqlConnection con, UUID columnId, String defaultValue);

  void notNull(EsqlConnection con, UUID columnId, String notNull);

  void columnMetadata(EsqlConnection con, UUID columnId, Metadata metadata);

  void dropColumn(EsqlConnection con, UUID columnId);

  void constraint(EsqlConnection con, UUID tableId, ConstraintDefinition constraint);

  void dropConstraint(EsqlConnection con, UUID tableId, String constraintName);

  Database NULL_DB = new Database() {
    @Override
    public void init(Configuration config) {}

    @Override
    public Configuration config() {
      return Configuration.EMPTY;
    }

    @Override
    public Structure structure() {
      return new Structure(this);
    }

    @Override
    public void postInit(Connection con, Structure structure) {}

    @Override
    public Translatable.Target target() {
      return Translatable.Target.ESQL;
    }

    @Override
    public <E extends Extension> E extension(Class<? extends Extension> e) throws NotFoundException {
      throw new NotFoundException("Extension " + e + " not loaded");
    }

    @Override
    public void addEsqlTransformer(EsqlTransformer transformer) {}

    @Override
    public boolean removeEsqlTransformer(EsqlTransformer transformer) {
      return false;
    }

    @Override
    public List<EsqlTransformer> esqlTransformers() {
      return Collections.emptyList();
    }

    @Override
    public Connection pooledConnection(boolean autoCommit, int isolationLevel) {
      return null;
    }

    @Override
    public Connection rawConnection(boolean autoCommit, int isolationLevel, String username, String password) {
      return null;
    }

    @Override
    public EsqlConnection esql(Connection con) {
      return null;
    }

    @Override
    public <T> T[] getArray(ResultSet rs, String index, Class<T> componentType) throws SQLException {
      return null;
    }

    @Override
    public <T> T[] getArray(ResultSet rs, int index, Class<T> componentType) {
      return null;
    }

    @Override
    public void setArray(PreparedStatement ps, int paramIndex, Object array) {}

    @Override
    public void addTable(EsqlConnection con, BaseRelation table) {}

    @Override
    public UUID tableId(EsqlConnection con, String tableName) {
      return null;
    }

    @Override
    public BaseRelation updateTable(EsqlConnection con, BaseRelation table) {
      return null;
    }

    @Override
    public void renameTable(EsqlConnection con, UUID tableId, String name) {}

    @Override
    public void clearTableMetadata(EsqlConnection con, UUID tableId) {}

    @Override
    public void tableMetadata(EsqlConnection con, UUID tableId, Metadata metadata) {}

    @Override
    public void dropTable(EsqlConnection con, UUID tableId) {}

    @Override
    public void column(EsqlConnection con, UUID tableId, Column column) {}

    @Override
    public void columnName(EsqlConnection con, UUID columnId, String name) {}

    @Override
    public void columnType(EsqlConnection con, UUID columnId, String type) {}

    @Override
    public void defaultValue(EsqlConnection con, UUID columnId, String defaultValue) {}

    @Override
    public void notNull(EsqlConnection con, UUID columnId, String notNull) {}

    @Override
    public void columnMetadata(EsqlConnection con, UUID columnId, Metadata metadata) {}

    @Override
    public void dropColumn(EsqlConnection con, UUID columnId) {}

    @Override
    public void constraint(EsqlConnection con, UUID tableId, ConstraintDefinition constraint) {}

    @Override
    public void dropConstraint(EsqlConnection con, UUID tableId, String constraintName) {}
  };
}
