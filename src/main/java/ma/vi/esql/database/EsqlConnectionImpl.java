/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static ma.vi.base.lang.Errors.unchecked;

/**
 * The implementation of {@link EsqlConnection}.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EsqlConnectionImpl implements EsqlConnection {
  public EsqlConnectionImpl(Database db, Connection con) {
    this.db = db;
    this.con = con;
  }

  public Connection connection() {
    return con;
  }

  @Override
  public Database database() {
    return db;
  }

  @Override
  public void commit() {
    unchecked(con::commit);
  }

  @Override
  public void rollback() {
    try {
      con.rollback();
    } catch (SQLException sqle) {
      throw new RuntimeException(sqle);
    }
  }

  @Override
  public void rollbackOnly() {
    rollbackOnly = true;
  }

  @Override
  public boolean isRollbackOnly() {
    return rollbackOnly;
  }

  /**
   * Increments the open count, returning its new value.
   */
  protected int incOpenCount() {
    try                       { assert !con.isClosed(); }
    catch (SQLException sqle) { throw new RuntimeException(sqle); }
    return openCount.incrementAndGet();
  }

  /**
   * @return The current open-count of this connection; used primarily for testing.
   */
  public int openCount() {
    return openCount.get();
  }

  /**
   * Decrement the openCount and closes the underlying connection if the openCount
   * is 0, committing the current transaction if the connection is not in auto-commit
   * mode or rollback-only mode, otherwise rolling back the ongoing transaction.
   */
  @Override
  public void close() {
    int count = openCount.decrementAndGet();
    if (count == 0) {
      try {
        try {
          if (!con.getAutoCommit()) {
            if (isRollbackOnly()) con.rollback();
            else                  con.commit();
          }
        } finally {
          con.close();
        }
      } catch (SQLException sqle) {
        throw new RuntimeException(sqle);
      }
    }
  }

  protected final Database db;

  private boolean rollbackOnly;

  /**
   * Underlying connection.
   */
  protected final Connection con;

  /**
   * Connections can be shared by different functions in the same thread. The
   * {@link Database#esql()}, when called without a new SQL connection, will
   * reuse the active transaction bound to the current thread, if any, or create
   * a new ESQL connection which is then bound to the current thread. This variable
   * is used to keep track of the number of times that this connection has been
   * "opened" in a try-with-resources in this manner so that it can be closed at
   * the end of the block that created it. Initially this is set to one and
   * incremented at each new "opening" (through `try (EsqlConnection c = db.esql())`)
   * and decremented at each closing of the try-with-resources block. When it
   * reaches, the transaction is committed (or rollbacked) and the underlying SQL
   * connection is actually closed.
   */
  private final AtomicInteger openCount = new AtomicInteger(1);
}