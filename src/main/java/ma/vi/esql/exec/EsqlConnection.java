/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.base.reflect.Dissector;
import ma.vi.base.reflect.Property;
import ma.vi.esql.database.Database;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.Parser;

import java.sql.Connection;
import java.util.Map;

import static ma.vi.base.lang.Errors.unchecked;

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
   * Executes the ESQL statement using this connection after inserting the
   * provided optional parameters.
   *
   * @param esql   The esql statement to execute.
   * @param params An optional list of parameters matching the parameter
   *               placeholders in the statement.
   * @return The result produced by the esql command on execution.
   */
  <R> R exec(Esql<?, ?> esql, Param... params);

  default <R> Iterable<R> exec(Esql<?, ?>           esql,
                               ResultTransformer<R> transformer,
                               Object               objectAsParams) {
    return exec(esql, transformer, asParams(objectAsParams));
  }

  default <R> R exec(Esql<?, ?> esql, Object objectAsParams) {
    return exec(esql, asParams(objectAsParams));
  }

  default <R> R exec(String esql, Param... params) {
    return exec(new Parser(database().structure()).parse(esql), params);
  }

  default <R> R exec(String esql, Object objectAsParams) {
    return exec(new Parser(database().structure()).parse(esql), asParams(objectAsParams));
  }

  default <R> Iterable<R> exec(String               esql,
                               ResultTransformer<R> transformer) {
    return transformer.transform(exec(new Parser(database().structure()).parse(esql)));
  }

  default <R> Iterable<R> exec(String               esql,
                               ResultTransformer<R> transformer,
                               Object               objectAsParams) {
    return exec(esql, transformer, asParams(objectAsParams));
  }

  default <R> Iterable<R> exec(String               esql,
                               ResultTransformer<R> transformer,
                               Param...             params) {
    return transformer.transform(exec(new Parser(database().structure()).parse(esql), params));
  }

  /**
   * @return The database that this connection works on.
   */
  Database database();

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
  Connection con();

  /**
   * Closes the connection, committing if the connection is not in auto-commit
   * or rollback-only mode, otherwise rolling back the ongoing transaction.
   */
  @Override
  default void close() {
    try {
      if (!con().getAutoCommit()) {
        if (isRollbackOnly()) {
          con().rollback();
        } else {
          con().commit();
        }
      }
    } catch (Exception sqle) {
      unchecked(() -> {
        if (!con().getAutoCommit()) {
          try {
            con().rollback();
          } catch (Exception e) {
            throw unchecked(e);
          }
        }
      });
    } finally {
      unchecked(con()::close);
    }
  }

  private static Param[] asParams(Object objectAsParams) {
    Map<String, Property> props = Dissector.properties(objectAsParams.getClass());
    Param[] params = new Param[props.size()];
    int i = 0;
    for (Map.Entry<String, Property> prop: props.entrySet()) {
      params[i] = Param.of(prop.getKey(), prop.getValue().get(objectAsParams));
      i++;
    }
    return params;
  }

  EsqlConnection NULL_CONNECTION = new EsqlConnection() {
    @Override
    public <R> R exec(Esql<?, ?> esql, Param... params) {
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

    @Override
    public void rollbackOnly() {}

    @Override
    public boolean isRollbackOnly() {
      return false;
    }

    @Override
    public Connection con() {
      return null;
    }
  };
}