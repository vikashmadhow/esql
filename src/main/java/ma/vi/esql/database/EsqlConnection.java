/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import ma.vi.esql.exec.Executor;
import ma.vi.esql.exec.QueryParams;
import ma.vi.esql.exec.ResultTransformer;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.Parser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * An interface representing a connection to the database through which ESQL
 * statements can be sent. An ESQL connection will normally wrap and specialise
 * a JDBC database connection for querying the database using ESQL, but still
 * allowing direct access to the underlying connection for when SQL commands
 * need to be sent.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface EsqlConnection extends AutoCloseable {

  /**
   * @return The database that this connection works on.
   */
  Database database();

  String user();

  void user(String user);

  /**
   * @return A unique identifier of the single active transaction of this connection.
   */
  String transactionId();

  /**
   * {@link #prepare(Esql, QueryParams) Prepares} and {@link #execPrepared(Esql, QueryParams) executes}
   * the ESQL program using this connection. Preparation includes parameter substitution,
   * macro expansion and filter applications.
   *
   * @param esql The esql statement to execute.
   * @param qp Parameters to the esql including named parameter values and filters.
   * @return The result produced by the esql command on execution.
   */
  default <R> R exec(Esql<?, ?> esql, QueryParams qp) {
    Iterator<Executor> executors = database().executors();
    Executor exec = executors.next().with(this, executors);
    return exec.execPrepared(exec.prepare(esql, qp), qp);
  }

  default <R> R exec(Esql<?, ?> esql) {
    Iterator<Executor> executors = database().executors();
    return executors.next().with(this, executors).exec(esql, null);
  }

  default <R> R exec(String esql, QueryParams qp) {
    Iterator<Executor> executors = database().executors();
    return executors.next().with(this, executors)
                           .exec(new Parser(database().structure()).parse(esql), qp);
  }

  default <R> R exec(String esql) {
    Iterator<Executor> executors = database().executors();
    return executors.next().with(this, executors)
                           .exec(esql, null);
  }

  default <R> Iterable<R> exec(Esql<?, ?>           esql,
                               ResultTransformer<R> transformer,
                               QueryParams          qp) {
    Iterator<Executor> executors = database().executors();
    return transformer.transform(executors.next().with(this, executors).exec(esql, qp));
  }

  default <R> Iterable<R> exec(Esql<?, ?>           esql,
                               ResultTransformer<R> transformer) {
    return exec(esql, transformer, null);
  }

  default <R> Iterable<R> exec(String               esql,
                               ResultTransformer<R> transformer,
                               QueryParams          qp) {
    return exec(new Parser(database().structure()).parse(esql), transformer, qp);
  }

  default <R> Iterable<R> exec(String               esql,
                               ResultTransformer<R> transformer) {
    return exec(esql, transformer, null);
  }

  /**
   * Prepares the query by substituting parameters, expanding macros, computing
   * types, applying filters, and so on.
   */
  default Esql<?, ?> prepare(Esql<?, ?> esql, QueryParams qp) {
    Iterator<Executor> executors = database().executors();
    return executors.next().with(this, executors).prepare(esql, qp);
  }

  default Esql<?, ?> prepare(Esql<?, ?> esql) {
    return prepare(esql, null);
  }

  default Esql<?, ?> prepare(String esql, QueryParams qp) {
    return prepare(new Parser(database().structure()).parse(esql), qp);
  }

  default Esql<?, ?> prepare(String esql) {
    return prepare(esql, null);
  }

  /**
   * Execute a prepared esql program.
   */
  default <R> R execPrepared(Esql<?, ?> esql, QueryParams qp) {
    Iterator<Executor> executors = database().executors();
    return executors.next().with(this, executors)
                           .execPrepared(esql, qp);
  }

  default <R> R execPrepared(Esql<?, ?> esql) {
    return execPrepared(esql, null);
  }

  /**
   * Add a structure change event that happened in this connection. A structure
   * change event is logged whenever a database object is created, dropped or
   * changed. Logged structure change events are sent to subscribers of such
   * events on transaction commit.
   * @param event Change event to log.
   */
  void addStructureChange(Database.StructureChangeEvent event);

  /**
   * Sets the connection on rollback-only mode meaning that any executed
   * statements will be automatically rolled back when the connection is
   * closed.
   */
  void rollbackOnly();

  /**
   * Underlying JDBC connection.
   */
  Connection connection();

  /**
   * @return True if the underlying connection is closed, false otherwise.
   */
  default boolean isClosed() {
    try {
      return connection().isClosed();
    } catch (SQLException sqle) {
      throw new RuntimeException("Could not get status of underlying connection", sqle);
    }
  }

  @Override void close();

  EsqlConnection NULL_CONNECTION = new EsqlConnection() {
    @Override
    public Esql<?, ?> prepare(Esql<?, ?> esql, QueryParams qp) {
      return null;
    }

    @Override
    public <R> R execPrepared(Esql<?, ?> esql, QueryParams qp) {
      return null;
    }

    @Override
    public Database database() {
      return Database.NULL_DB;
    }

    @Override
    public String user() {
      return null;
    }

    @Override
    public void user(String user) {}

    @Override
    public String transactionId() {
      return null;
    }

    @Override
    public void addStructureChange(Database.StructureChangeEvent event) {}

    @Override public void close() {}

    @Override
    public void rollbackOnly() {}

    @Override
    public Connection connection() {
      return null;
    }
  };
}