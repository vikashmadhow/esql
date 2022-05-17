/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import ma.vi.esql.exec.Result;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.emptyList;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * The implementation of {@link EsqlConnection}.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class EsqlConnectionImpl implements EsqlConnection {
  public EsqlConnectionImpl(Database db, Connection con) {
    this(db, con, "___unknown");
  }

  public EsqlConnectionImpl(Database db, Connection con, String user) {
    this.db = db;
    this.con = con;
    this.user = user;
    this.transaction = db.transactionId(con);
  }

  @Override
  public Database database() {
    return db;
  }

  public Connection connection() {
    return con;
  }

  @Override
  public String user() {
    return user;
  }

  @Override
  public String transactionId() {
    return transaction;
  }

  @Override
  public void rollbackOnly() {
    rollbackOnly = true;
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
      boolean committed = false;
      try {
        try {
          if (!con.getAutoCommit()) {
            if (rollbackOnly) { con.rollback(); }
            else {
              if (db.target() == SQLSERVER) {
                con.createStatement().executeUpdate("commit transaction");
              }
              con.commit();
              committed = true;
            }
          }
        } finally {
          con.close();
        }
      } catch (SQLException sqle) {
        throw new RuntimeException(sqle);
      }

      if (committed) {
        /*
         * Finalise history tracking and send notifications to subscribers, if
         * any, asynchronously.
         */
        FinaliseThreadPool.execute(() -> {
          Set<String> tables = new HashSet<>();
          try (Connection con = database().pooledConnection();
               ResultSet rs = con.createStatement().executeQuery(
                "select distinct table_name"
                  + "  from _core._temp_history"
                  + " where trans_id='" + transaction + "'")) {
            while (rs.next()) tables.add(rs.getString(1));
          } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
          }

          Structure structure = database().structure();
          if (!tables.isEmpty()) {
            try {
              try (Connection con = database().pooledConnection()) {
                /*
                 * Move to transaction history and set user.
                 */
                con.createStatement().executeUpdate(
                  "insert into _core.history "
                    + "select trans_id, table_name, event, at_time, " + (user == null ? "null" : "'" + user + "'")
                    + "  from _core._temp_history"
                    + " where trans_id='" + transaction + "'");

                /*
                 * Clean temporary history
                 */
                con.createStatement().executeUpdate(
                  "delete from _core._temp_history"
                    + " where trans_id='" + transaction + "'");
                con.commit();
              }

              if (user != null) {
                /*
                 * Set user in fine-grained history.
                 */
                try (Connection con = database().pooledConnection()) {
                  for (String table : tables) {
                    if (structure.relationExists(table + ".History")) {
                      con.createStatement().executeUpdate(
                          "update " + table + ".History"
                        + "   set _hist_user='" + user + "'"
                        + " where _hist_trans_id='" + transaction + "'");
                    }
                  }
                  con.commit();
                }
              }
            } catch (SQLException sqle) {
              throw new RuntimeException(sqle);
            }
          }

          /*
           * Notify subscribers
           */
          for (String table: tables) {
            List<Database.Subscription> subscriptions = database().subscriptions(table);
            List<Map<String, Object>> inserted    = new ArrayList<>();
            List<Map<String, Object>> deleted     = new ArrayList<>();
            List<Map<String, Object>> updatedFrom = new ArrayList<>();
            List<Map<String, Object>> updatedTo   = new ArrayList<>();
            String historyTable = table + ".History";
            if (structure.relationExists(historyTable)
              && subscriptions.stream().anyMatch(Database.Subscription::includeHistory)) {
              /*
               * History is tracked for this table and one or more
               * subscriptions require the history: load the history.
               */
              try (EsqlConnection con = database().esql(database().pooledConnection());
                   Result rs = con.exec("select *"
                                          + "  from " + historyTable
                                          + " where _hist_trans_id='" + transaction + "'")) {
                while (rs.toNext()) {
                  String event = rs.value("_hist_event");
                  switch (event) {
                    case "I" -> inserted    .add(rs.valueRow());
                    case "D" -> deleted     .add(rs.valueRow());
                    case "F" -> updatedFrom .add(rs.valueRow());
                    default  -> updatedTo   .add(rs.valueRow());
                  }
                }
              }
            }
            for (Database.Subscription subscription: subscriptions) {
              try {
                subscription.events().put(
                  subscription.includeHistory()
                  ? new Database.ChangeEvent(table, user, new Date(),
                                             inserted,    deleted,
                                             updatedFrom, updatedTo)
                  : new Database.ChangeEvent(table, user, new Date(),
                                             emptyList(), emptyList(),
                                             emptyList(), emptyList()));
              } catch (InterruptedException ignore) {}
            }
          }
        });
      }
    }
  }

  /**
   * The database that this connection operates on.
   */
  protected final Database db;

  /**
   * When set to true the active transaction on this connection will be rolled
   * back when the connection is closed.
   */
  private boolean rollbackOnly;

  /**
   * Underlying connection.
   */
  protected final Connection con;

  /**
   * User for whom this connection has been created.
   */
  protected final String user;

  /**
   * The id of the current transaction.
   */
  protected final String transaction;

  /**
   * Connections can be shared by different functions in the same thread. When
   * {@link Database#esql()} is invoked without a new SQL connection, it will
   * reuse the active connection bound to the current thread, if any, or create
   * a new ESQL connection which is then bound to the current thread.
   *
   * This variable is used to keep track of the number of times that this connection
   * has been "opened" in a try-with-resources in this manner so that it can be
   * closed at the end of the block that created it. Initially this is set to 1
   * and incremented at each new "opening" (through `try (EsqlConnection c = db.esql())`)
   * and decremented at each closing of the try-with-resources block. When it
   * reaches the end of the try-with-resourced block that created it, the
   * transaction is committed (or rolled back) and the underlying SQL connection
   * is closed.
   */
  private final AtomicInteger openCount = new AtomicInteger(1);

  /**
   * Thread pool to execute history finalisation after commit.
   */
  private static final ExecutorService FinaliseThreadPool = Executors.newCachedThreadPool();
}