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
    Executor exec = database().executor().with(this);
    return exec.execPrepared(exec.prepare(esql, qp), qp);
  }

  default <R> R exec(Esql<?, ?> esql) {
    return database().executor().with(this).exec(esql, null);
  }

  default <R> R exec(String esql, QueryParams qp) {
    return database().executor().with(this)
                     .exec(new Parser(database().structure()).parse(esql), qp);
  }

  default <R> R exec(String esql) {
    return database().executor().with(this)
                     .exec(esql, null);
  }

  default <R> Iterable<R> exec(Esql<?, ?>           esql,
                               ResultTransformer<R> transformer,
                               QueryParams          qp) {
    return transformer.transform(database().executor()
                                           .with(this)
                                           .exec(esql, qp));
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
    return database().executor()
                     .with(this)
                     .prepare(esql, qp);
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
    return database().executor()
                     .with(this)
                     .execPrepared(esql, qp);
  }

  default <R> R execPrepared(Esql<?, ?> esql) {
    return execPrepared(esql, null);
  }

  /**
   * Commits the current transaction on this connection.
   */
  void commit();

  /**
   * Rolls back the current transaction on this connection.
   */
  void rollback();

  /**
   * Sets the connection on rollback-only mode meaning that any executed
   * statements will be automatically rolled back when the connection is
   * closed.
   */
  void rollbackOnly();

  /**
   * @return true if {@link #rollbackOnly()} was previoulsy called on this
   *         connection setting it to rollback-only mode.
   */
  boolean isRollbackOnly();

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
    public void commit() {}

    @Override
    public void rollback() {}

    @Override public void close() {}

    @Override
    public void rollbackOnly() {}

    @Override
    public boolean isRollbackOnly() {
      return false;
    }

    @Override
    public Connection connection() {
      return null;
    }
  };
}