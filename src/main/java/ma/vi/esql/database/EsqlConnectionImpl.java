/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import ma.vi.esql.exec.Result;
import ma.vi.esql.semantic.type.Type;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.Logger.Level.WARNING;
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
  public void user(String user) {
    this.user = user;
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
        var subscriptions = database().subscriptions();
        var structureSubscriptions = database().structureSubscriptions();
        var transactionId = transactionId();
        FinaliseThreadPool.execute(() -> {
          Map<String, Set<String>> tableEvents = new HashMap<>();
          try (Connection con = database().pooledConnection()) {
            Statement st = con.createStatement();
            if (db.target() == SQLSERVER) {
              st.executeUpdate("set lock_timeout 100");
            } else {
              st.executeUpdate("set local lock_timeout='100ms'");
            }
            int tries = 0;
            boolean transRead = false;
            while (tries < MAX_RETRIES && !transRead) {
              try (ResultSet rs = st.executeQuery("""
                     select distinct table_name, event
                       from _core._temp_history
                      where trans_id='""" + transactionId + "'")) {
                transRead = true;
                while (rs.next())
                  tableEvents.computeIfAbsent(rs.getString(1), k -> new HashSet<>()).add(rs.getString(2));
                if (db.target() == SQLSERVER) {
                  st.executeUpdate("set lock_timeout -1");
                } else {
                  st.executeUpdate("set local lock_timeout='0ms'");
                }
              } catch (SQLException sqle) {
                tries++;
//                log.log(WARNING, " >>> Could not obtain a lock on _core._temp_history "
//                               + "table to read coarse transaction history. Tried "
//                               + tries + " times");
                try { Thread.sleep(10); } catch(InterruptedException ignored) {}
              }
            }
          } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
          }

          Structure structure = database().structure();
          if (!tableEvents.isEmpty()) {
            try {
              int tries = 0;
              boolean transferred = false;
              while (tries < MAX_RETRIES && !transferred) {
                Connection con = null;
                try {
                  con = database().pooledConnection();
                  Statement st = con.createStatement();
                  if (db.target() == SQLSERVER) {
                    st.executeUpdate("set lock_timeout 100");
                  } else {
                    st.executeUpdate("set local lock_timeout='100ms'");
                  }
                  /*
                   * Move to transaction history and set user.
                   */
                  st.executeUpdate(
                        "insert into _core.history(trans_id, table_name, event, user_id, at_time) "
                      + "select trans_id, table_name, event, " + (user == null ? "null" : "'" + user + "', min(at_time)")
                      + "  from _core._temp_history"
                      + " where trans_id='" + transactionId + "'"
                      + " group by trans_id, table_name, event");

                  /*
                   * Clean temporary history
                   */
                  st.executeUpdate(
                        "delete from _core._temp_history"
                      + " where trans_id='" + transactionId + "'");

                  if (db.target() == SQLSERVER) {
                    st.executeUpdate("set lock_timeout -1");
                  } else {
                    st.executeUpdate("set local lock_timeout='0ms'");
                  }

                  con.commit();
                  transferred = true;
                } catch (SQLException sqle) {
                  tries++;
//                  log.log(WARNING, " >>> Could not obtain a lock on _core._temp_history "
//                    + "table to transfer coarse transaction history. Tried "
//                    + tries + " times");

                  if (db.target() == SQLSERVER) {
                    con.createStatement().executeUpdate("set lock_timeout -1");
                  } else {
                    con.createStatement().executeUpdate("set local lock_timeout='0ms'");
                  }

                  try { Thread.sleep(10); } catch(InterruptedException ignored) {}
                } finally {
                  if (con != null) {
                    con.close();
                  }
                }
              }

              if (user != null) {
                /*
                 * Set user in fine-grained history.
                 */
                try (Connection con = database().pooledConnection()) {
                  for (String table: tableEvents.keySet()) {
                    if (structure.relationExists(table + "$history")) {
                      con.createStatement().executeUpdate(
                          "update " + Type.dbTableName(table + "$history", db.target())
                        + "   set history_change_user='" + user + "'"
                        + " where history_change_trans_id='" + transactionId + "'");
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
           * Notify structure change subscribers.
           */
          for (Database.StructureSubscription subscription: structureSubscriptions) {
            try {
              for (var e: structureChanges) subscription.events().put(e);
            } catch (InterruptedException ignore) {}
          }

          /*
           * Notify subscribers
           */
          for (String table: tableEvents.keySet()) {
            List<Database.Subscription> tableSubscriptions = subscriptions.getOrDefault(table, emptyList());
            List<Map<String, Object>> inserted    = new ArrayList<>();
            List<Map<String, Object>> deleted     = new ArrayList<>();
            List<Map<String, Object>> updatedFrom = new ArrayList<>();
            List<Map<String, Object>> updatedTo   = new ArrayList<>();
            String historyTable = table + "$history";
            if (structure.relationExists(historyTable)
             && tableSubscriptions.stream().anyMatch(Database.Subscription::includeHistory)) {
              /*
               * History is tracked for this table and one or more subscriptions
               * require the history: load the history.
               */
              try (EsqlConnection con = database().esql(database().pooledConnection());
                   Result rs = con.exec("select *"
                                      + "  from " + historyTable
                                      + " where history_change_trans_id='" + transactionId + "'")) {
                while (rs.toNext()) {
                  String event = rs.value("history_change_event");
                  switch (Database.Change.from(event)) {
                    case INSERTED     -> inserted    .add(rs.valueRow());
                    case DELETED      -> deleted     .add(rs.valueRow());
                    case UPDATED_FROM -> updatedFrom .add(rs.valueRow());
                    default           -> updatedTo   .add(rs.valueRow());
                  }
                }
              }
            }
            Set<String> events = tableEvents.get(table);
            for (Database.Subscription subscription: tableSubscriptions) {
              try {
                subscription.events().put(
                  subscription.includeHistory()
                ? new Database.ChangeEvent(table, user, new Date(),
                                           events.contains(Database.Change.INSERTED.code),
                                           events.contains(Database.Change.DELETED.code),
                                           events.contains(Database.Change.UPDATED.code),
                                           inserted,    deleted,
                                           updatedFrom, updatedTo)
                : new Database.ChangeEvent(table, user, new Date(),
                                           events.contains(Database.Change.INSERTED.code),
                                           events.contains(Database.Change.DELETED.code),
                                           events.contains(Database.Change.UPDATED.code),
                                           emptyList(), emptyList(),
                                           emptyList(), emptyList()));
              } catch (InterruptedException ignore) {}
            }
          }
        });
      }
    }
  }

  @Override
  public void addStructureChange(Database.StructureChangeEvent event) {
    structureChanges.add(event);
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
  protected String user;

  /**
   * The id of the current transaction.
   */
  protected final String transaction;

  /**
   * Connections can be shared by different functions in the same thread. When
   * {@link Database#esql()} is invoked without a new SQL connection, it will
   * reuse the active connection bound to the current thread, if any, or create
   * a new ESQL connection which is then bound to the current thread.
   * <p>
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
   * The list of DDL changes to be sent to subscribers on transaction commit.
   */
  private final List<Database.StructureChangeEvent> structureChanges = new ArrayList<>();

  /**
   * Thread pool to execute history finalisation after commit.
   */
  private static final ExecutorService FinaliseThreadPool = Executors.newCachedThreadPool();

  /**
   * Number of retries to obtain lock on _core._temp_history.
   */
  private static final int MAX_RETRIES = 1;

  private static final System.Logger log = System.getLogger(EsqlConnectionImpl.class.getName());
}