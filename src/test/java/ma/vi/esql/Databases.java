/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql;

import ma.vi.base.config.Configuration;
import ma.vi.esql.database.*;

import static ma.vi.esql.database.Database.*;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Databases {
  public static HSqlDb HSqlDb() {
    if (hSqlDb == null) {
      String userHome = System.getProperty("user.home");
      hSqlDb = new HSqlDb(Configuration.of(
          CONFIG_DB_NAME, "file:" + userHome + "/testdb/data",
          CONFIG_DB_USER, "SA",
          CONFIG_DB_PASSWORD, ""));
    }
    return hSqlDb;
  }

  public static Postgresql Postgresql() {
    if (postgresql == null) {
      postgresql = new Postgresql(Configuration.of(
          CONFIG_DB_NAME, "test",
          CONFIG_DB_USER, "test",
          CONFIG_DB_PASSWORD, "test"));
//      createTestTables(postgresql);
    }
    return postgresql;
  }

  public static SqlServer SqlServer() {
    if (sqlServer == null) {
      sqlServer = new SqlServer(Configuration.of(
          CONFIG_DB_NAME, "test",
          CONFIG_DB_USER, "test",
          CONFIG_DB_PASSWORD, "test"));
//      createTestTables(sqlServer);
    }
    return sqlServer;
  }

  public static MariaDb MariaDb() {
    if (mariaDb == null) {
      mariaDb = new MariaDb(Configuration.of(
          CONFIG_DB_NAME, "test",
          CONFIG_DB_USER, "test",
          CONFIG_DB_PASSWORD, "test"));
//      createTestTables(mariaDb);
    }
    return mariaDb;
  }

  public static TestDatabase TestDatabase() {
    if (testDatabase == null) {
      testDatabase = new TestDatabase();
    }
    return testDatabase;
  }

  public static Database[] databases() {
    return new Database[] {
        TestDatabase(),
        HSqlDb(),
        Postgresql(),
        SqlServer()
    };
  }

  public static Database[] externalDatabases() {
    return new Database[] {
        HSqlDb(),
        Postgresql(),
        MariaDb(),
        SqlServer()
    };
  }

  private static TestDatabase testDatabase;
  private static HSqlDb hSqlDb;
  private static SqlServer sqlServer;
  private static MariaDb mariaDb;
  private static Postgresql postgresql;
}
