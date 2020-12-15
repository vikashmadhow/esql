package ma.vi.esql;

import ma.vi.esql.database.Database;
import ma.vi.esql.database.HSqlDb;
import ma.vi.esql.database.Postgresql;
import ma.vi.esql.database.SqlServer;

import java.util.Map;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Databases {
  public static HSqlDb HSqlDb() {
    if (hSqlDb == null) {
      hSqlDb = new HSqlDb(Map.of(
          "database.name", "mem:test",
          "database.user.name", "SA",
          "database.user.password", ""), true, true);
    }
    return hSqlDb;
  }

  public static Postgresql Postgresql() {
    if (postgresql == null) {
      postgresql = new Postgresql(Map.of(
          "database.name", "test",
          "database.user.name", "test",
          "database.user.password", "test"), true, true);
    }
    return postgresql;
  }

  public static SqlServer SqlServer() {
    if (sqlServer == null) {
      sqlServer = new SqlServer(Map.of(
          "database.name", "test",
          "database.user.name", "test",
          "database.user.password", "test"), true, true);
    }
    return sqlServer;
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
        SqlServer()
    };
  }

  private static TestDatabase testDatabase;
  private static HSqlDb hSqlDb;
  private static SqlServer sqlServer;
  private static Postgresql postgresql;
}
