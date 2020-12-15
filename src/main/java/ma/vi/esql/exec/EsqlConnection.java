/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.esql.parser.Esql;

import java.sql.Connection;

import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.esql.parser.Esql.Target;

/**
 * An interface representing a connection to the database through
 * which ESQL statement can be sent. An ESQL connection will normally
 * wrap and specialise a JDBC database connection for querying the
 * database using ESQL, but still allowing direct access to the underlying
 * connection for when SQL commands need to be sent.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface EsqlConnection extends AutoCloseable {

  Target target();

  Result exec(Esql<?, ?> esql, Param... params);

  void commit();

  void rollback();

  void rollbackOnly();

  boolean isRollbackOnly();

  /**
   * Underlying JDBC connection.
   */
  Connection con();

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
}