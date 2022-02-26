/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ma.vi.base.config.Configuration;
import ma.vi.esql.translation.Translatable;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.util.Properties;

import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.esql.translation.Translatable.Target.MARIADB;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class MariaDb extends AbstractDatabase {
  public MariaDb(Configuration config) {
    Properties props = new Properties();
    props.setProperty("dataSourceClassName", MariaDbDataSource.class.getName());
    props.setProperty("dataSource.serverName", valueOf(config.get(CONFIG_DB_HOST, "localhost")));
    if (config.has(CONFIG_DB_PORT)) {
      props.setProperty("dataSource.portNumber", valueOf(config.get(CONFIG_DB_PORT)));
    }

    database =  valueOf(config.get(CONFIG_DB_NAME));
    props.setProperty("dataSource.databaseName", database);
    props.setProperty("dataSource.user", valueOf(config.get(CONFIG_DB_USER)));
    props.setProperty("dataSource.password", valueOf(config.get(CONFIG_DB_PASSWORD)));
    dataSource = new HikariDataSource(new HikariConfig(props));

    init(config);
    postInit(pooledConnection(), structure());
  }

  @Override
  public Translatable.Target target() {
    return MARIADB;
  }

  @Override
  public void postInit(Connection con, Structure structure) {
    super.postInit(con, structure);
    try (Connection c = pooledConnection(true, -1)) {
      // MariaDB specific
    } catch (SQLException e) {
      throw unchecked(e);
    }
  }

  @Override
  public Connection rawConnection(boolean autoCommit,
                                  int isolationLevel,
                                  String username,
                                  String password) {
    try {
      String db = valueOf(config().get(CONFIG_DB_NAME));
      Connection con = DriverManager.getConnection(
          "jdbc:mariadb://"
              + valueOf(config().get(CONFIG_DB_HOST)) + ':'
              + (config().has(CONFIG_DB_PORT)
                    ? ':' + valueOf(config().get(CONFIG_DB_PORT))
                    : "")
              + '/' + db,
          username,
          password);

      con.setAutoCommit(autoCommit);
      con.createStatement().executeUpdate("ALTER DATABASE " + db
                                        + " CHARACTER SET = utf8mb4 "
                                        + " COLLATE = utf8mb4_unicode_ci");
      con.createStatement().executeUpdate("SET sql_mode='ANSI_QUOTES'");
      if (isolationLevel != -1) {
        con.setTransactionIsolation(isolationLevel);
      } else {
        con.setTransactionIsolation(isolationLevel);
      }
      return con;
    } catch (Exception e) {
      throw unchecked(e);
    }
  }

  @Override
  public Connection pooledConnection(boolean autoCommit,
                                     int isolationLevel,
                                     String username,
                                     String password) {
    try {
//      Connection con = dataSource.getConnection(username, password);
      Connection con = dataSource.getConnection();
      con.setAutoCommit(autoCommit);
      con.createStatement().executeUpdate("ALTER DATABASE " + database
                                              + " CHARACTER SET = utf8mb4 "
                                              + " COLLATE = utf8mb4_unicode_ci");
      con.createStatement().executeUpdate("SET sql_mode='ANSI_QUOTES'");
      if (isolationLevel != -1) {
        con.setTransactionIsolation(isolationLevel);
      }
      return con;
    } catch (Exception e) {
      throw unchecked(e);
    }
  }

  @Override
  public void setArray(PreparedStatement ps, int paramIndex, Object array) throws SQLException {
    SqlServer._setArray(ps, paramIndex, array);
  }

  @Override
  public <T> T[] getArray(ResultSet rs, String index, Class<T> componentType) throws SQLException {
    return SqlServer.arrayOf(rs.getString(index), componentType);
  }

  @Override
  public <T> T[] getArray(ResultSet rs, int index, Class<T> componentType) throws SQLException {
    return SqlServer.arrayOf(rs.getString(index), componentType);
  }

  /**
   * Datasource for pooled connections.
   */
  private final HikariDataSource dataSource;

  private final String database;
}
