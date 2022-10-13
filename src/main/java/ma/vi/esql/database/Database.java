/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import ma.vi.base.config.Configuration;
import ma.vi.base.lang.NotFoundException;
import ma.vi.base.tuple.T2;
import ma.vi.esql.exec.Executor;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Column;
import ma.vi.esql.semantic.type.Struct;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.EsqlTransformer;
import ma.vi.esql.syntax.define.Metadata;
import ma.vi.esql.syntax.define.table.ConstraintDefinition;
import ma.vi.esql.translation.Translatable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.BlockingQueue;

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

//  /**
//   * Configuration parameter for whether to create the core tables if
//   * missing; default is true.
//   */
//  String CONFIG_DB_CREATE_CORE_TABLES = "database.createCoreTables";

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
   *   <li><b>database.extensions:</b> A set of classes implementing the {@link Extension}
   *                                   interface which are initialised when the database
   *                                   starts.</li>
   * </ul>
   *
   * This method must be called when the database object is created. It is usually
   * automatically called from the constructor of the specific database object.
   *
   * @param config Contains the configuration keys for connecting to the database.
   */
  void init(Configuration config);

  /**
   * Returns the configuration with which the database was created.
   */
  Configuration config();

  /**
   * Returns the structure of the objects (relations) in the database, which is
   * usually populated when the database is initialised in {@link #init(Configuration)}.
   */
  Structure structure();

//  /**
//   * Whether to create the core tables (in which are stored metadata info
//   * on all tables and columns, among others), if they are not present. This
//   * method returns the value of the configuration parameter
//   * `database.createCoreTables` if it is present, or true otherwise.
//   */
//  default boolean createCoreTables() {
//    return config().get(CONFIG_DB_CREATE_CORE_TABLES, Boolean.TRUE);
//  }

//  /**
//   * Performs post initialisation of the database where special objects, such as
//   * tables to hold metadata on other tables, can be created in the database. This
//   * is called after the {@link #structure()} method which allows the use of ESQL
//   * to create the objects.
//   */
//  void postInit(Connection con, Structure structure);

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
  <E extends Extension, R extends E> R extension(Class<E> e) throws NotFoundException;

  /**
   * @return An iterator over the executor chain positioned at its front.
   */
  Iterator<Executor> executors();

  /**
   * Adds an executor at the front of the executor chain. The executor chain is
   * responsible for executing ESQL programs with the one in the front of the chain
   * invoked by the connection for execution, and provided with an iterator over
   * the chain positioned at the next executor on it. The current executor can
   * complete the execution and return the result or delegate to the next executor
   * in the chain (after, optionally, making any necessary changes to the execution
   * context).
   *
   * @param executor The executor to add to the front of the executor chain.
   */
  void addExecutor(Executor executor);

  /**
   * A subscription to change events on a table.
   *
   * @param table The table to observe.
   * @param includeHistory Whether to observe the full history of changes (i.e.
   *                       inserts, updates and deletes). Without full history,
   *                       only a change event without the detailed change data
   *                       will be generated and sent to the subscriber.
   * @param events The queue forming the subscription channel through the database
   *               will send change events to the subscriber.
   */
  record Subscription(String table,
                      boolean includeHistory,
                      BlockingQueue<ChangeEvent> events) {}

  record ChangeEvent(String table,
                     String user,
                     Date   at,
                     List<Map<String, Object>> inserted,
                     List<Map<String, Object>> deleted,
                     List<Map<String, Object>> updatedFrom,
                     List<Map<String, Object>> updatedTo) {}

  Subscription subscribe(String table, boolean includeHistory);

  default Subscription subscribe(String table) {
    return subscribe(table, false);
  }

  List<Subscription> subscriptions(String table);

  void unsubscribe(Subscription subscription);

  // Connections
  //////////////////////////////////////////////////////
  /**
   * Returns a connection from the pool to the underlying database.
   */
  Connection pooledConnection(int isolationLevel);

  default Connection pooledConnection() {
    return pooledConnection(-1);
  }

  /**
   * Returns a raw connection (unpooled) to the underlying database.
   * Raw connections provide access to database-specific functionalities
   * such as bulk copying through the CopyManager in Postgresql.
   */
  Connection rawConnection(int isolationLevel,
                           String username,
                           String password);

  default Connection rawConnection(int isolationLevel) {
    return rawConnection(isolationLevel,
                         config().get("database.user.name"),
                         config().get("database.user.password"));
  }

  default Connection rawConnection() {
    return rawConnection(-1);
  }

  default Connection rawConnection(String username, String password) {
    return rawConnection(-1, username, password);
  }

  /**
   * @return A unique identifier of the single active transaction of this connection.
   */
  String transactionId(Connection con);

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

  // Load and check database information
  /////////////////////////////////////////

  default boolean tableExists(EsqlConnection con, String table) {
    T2<String, String> name = Type.splitName(table);
    try(ResultSet rs = con.connection().createStatement().executeQuery(
      "select table_name "
        + "  from information_schema.tables"
        + " where table_schema='" + name.a + "'"
        + "   and table_name='" + name.b + "'")) {
      return rs.next();
    } catch (SQLException sqle) {
      throw new RuntimeException(sqle);
    }
  }

  /**
   * Creates the _core.relations table if it does not exist already.
   * @param con The connection to use to create the table.
   */
  default void createCoreRelations(EsqlConnection con) {
    if (!tableExists(con, "_core.relations")) {
      con.exec("""
               create table _core.relations drop undefined({
                 name: 'Relations',
                 description: 'All relations in the database'
               }
               
               _id             uuid    not null,
               name            string  not null,
               display_name    string,
               description     string,
               "type"          char    not null,
               view_definition text,
                                      
               constraint _core_relations_pk       primary key (_id),
               constraint _core_relations_unq_name unique(name))""");
    }
  }

  default void createCoreRelationAttributes(EsqlConnection con) {
    if (!tableExists(con, "_core.relation_attributes")) {
      /*
       * create table for holding additional type information on tables.
       */
      con.exec("""
               create table _core.relation_attributes drop undefined({
                 name: 'Relation attributes',
                 description: 'Attributes (metadata) of relations'
               }
               
               _id         uuid    not null,
               relation_id uuid    not null,
               attribute   string  not null,
               value       text,
               
               constraint _core_rel_attr_pk           primary key(_id),
               constraint _core_rel_attr_rel_fk       foreign key(relation_id) references _core.relations(_id) on delete cascade on update cascade,
               constraint _core_rel_attr_unq_rel_attr unique(relation_id, attribute))""");
    }
  }

  default void createCoreColumns(EsqlConnection con) {
    if (!tableExists(con, "_core.columns")) {
      con.exec("""
               create table _core.columns drop undefined({
                 name: 'Relation columns',
                 description: 'Information on columns of relations'
               }
               
               _id             uuid   not null,
               _no_delete      bool   not null default false,
               relation_id     uuid   not null,
               name            string not null,
               derived_column  bool,
               "type"          string not null,
               not_null        bool,
               expression      text,
               seq             int    not null,
               
               constraint _core_columns_pk                primary key(_id),
               constraint _core_columns_relation_id_fk    foreign key(relation_id) references _core.relations(_id) on delete cascade on update cascade,
               constraint _core_columns_unq_relation_name unique(relation_id, name))""");
//               constraint _core_columns_unq_relation_seq  unique(relation_id, seq))""");
    }
  }

  default void createCoreColumnAttributes(EsqlConnection con) {
    if (!tableExists(con, "_core.column_attributes")) {
      con.exec("""
               create table _core.column_attributes drop undefined({
                 name: 'Column attributes',
                 description: 'Attributes (metadata) of relation columns'
               }
               
               _id       uuid    not null,
               column_id uuid    not null,
               attribute string  not null,
               value     text,
               
               constraint _core_column_attr_pk              primary key(_id),
               constraint _core_column_attr_column_fk       foreign key(column_id) references _core.columns(_id) on delete cascade on update cascade,
               constraint _core_column_attr_unq_column_attr unique(column_id, attribute))""");
    }
  }

  default void createCoreConstraints(EsqlConnection con) {
    if (!tableExists(con, "_core.constraints")) {
      con.exec("""
               create table _core.constraints drop undefined({
                 name: 'Relation constraints',
                 description: 'Integrity constraints on relations'
               }
               
               _id                 uuid    not null,
               name                string  not null,
               relation_id         uuid    not null,
               "type"              char    not null,
               check_expr          text,
               source_columns      []string,
               target_relation_id  uuid,
               target_columns      []string,
               forward_cost        int     not null default 1,
               reverse_cost        int     not null default 2,
               on_update           char,
               on_delete           char,
               
               constraint _core_constraints_pk        primary key(_id),
               constraint _core_constraints_rel_id_fk foreign key(relation_id) references _core.relations(_id) on delete cascade on update cascade)""");
    }
  }

  default void createCoreHistory(EsqlConnection con) {
    if (!tableExists(con, "_core.history")) {
      /*
       * Coarse-grain history tracking.
       */
      con.exec("""
               create table _core.history drop undefined(
               trans_id   string   not null,
               table_name string   not null,
               event      char     not null,
               at_time    datetime not null,
               user_id    string)""");

      con.exec("create index history_trans_id on _core.history(trans_id)");

      /*
       * Temp history saved in this table for performance, before being moved
       * asynchronously to _core.history.
       */
      con.exec("""
               create table _core._temp_history drop undefined(
               trans_id   string   not null,
               table_name string   not null,
               event      char     not null,
               at_time    datetime not null)""");
    }
  }

//  List<BaseRelation> loadTables();
//
//  /**
//   * @param table The table to load indices for.
//   * @return The indices on the table.
//   */
//  List<Index> loadIndices(String table);

  // Update database information
  ///////////////////////////////////////////////////

  /**
   * Adds a table to the core information tables if latter are present.
   */
  void addTable(EsqlConnection con, Struct table);

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

  void column(EsqlConnection con, UUID tableId, Column column, int seq);

  void columnName(EsqlConnection con, UUID columnId, String name);

  void columnType(EsqlConnection con, UUID columnId, String type);

  void defaultValue(EsqlConnection con, UUID columnId, String defaultValue);

  void notNull(EsqlConnection con, UUID columnId, String notNull);

  void columnMetadata(EsqlConnection con, UUID columnId, Metadata metadata);

  void dropColumn(EsqlConnection con, UUID columnId);

  void constraint(EsqlConnection con, UUID tableId, ConstraintDefinition constraint);

  void dropConstraint(EsqlConnection con, UUID tableId, String constraintName);

  int SNAPSHOT_ISOLATION = 0x1000;

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
    public Translatable.Target target() {
      return Translatable.Target.ESQL;
    }

    @Override
    public <E extends Extension, R extends E> R extension(Class<E> e) throws NotFoundException {
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
    public Iterator<Executor> executors() {
      return Collections.emptyIterator();
    }

    @Override
    public void addExecutor(Executor executor) {}

    @Override
    public Subscription subscribe(String table, boolean includeHistory) {
      return null;
    }

    @Override
    public List<Subscription> subscriptions(String table) {
      return null;
    }

    @Override
    public void unsubscribe(Subscription subscription) {}

    @Override
    public Connection pooledConnection(int isolationLevel) {
      return null;
    }

    @Override
    public Connection rawConnection(int isolationLevel, String username, String password) {
      return null;
    }

    @Override
    public String transactionId(Connection con) {
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
    public void addTable(EsqlConnection con, Struct table) {}

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
    public void column(EsqlConnection con, UUID tableId, Column column, int seq) {}

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
