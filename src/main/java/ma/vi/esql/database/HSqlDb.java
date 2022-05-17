/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ma.vi.base.config.Configuration;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.translation.Translatable;
import org.hsqldb.Server;
import org.hsqldb.jdbc.JDBCDataSource;

import java.sql.*;
import java.util.Properties;

import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.esql.translation.Translatable.Target.HSQLDB;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class HSqlDb extends AbstractDatabase {
  public HSqlDb(Configuration config) {
    Properties props = new Properties();
    props.setProperty("dataSourceClassName", JDBCDataSource.class.getName());
    if (config.has(CONFIG_DB_HOST)) {
      props.setProperty("dataSource.serverName", valueOf(config.get(CONFIG_DB_HOST)));
    }
    if (config.has(CONFIG_DB_PORT)) {
      props.setProperty("dataSource.portNumber", valueOf(config.get(CONFIG_DB_PORT)));
    }
    String database = valueOf(config.get(CONFIG_DB_NAME));
    props.setProperty("dataSource.databaseName", database);
    if (config.get(CONFIG_DB_USER) != null
     && config.get(CONFIG_DB_PASSWORD) != null) {
      props.setProperty("dataSource.user", valueOf(config.get(CONFIG_DB_USER)));
      props.setProperty("dataSource.password", valueOf(config.get(CONFIG_DB_PASSWORD)));
    }
    dataSource = new HikariDataSource(new HikariConfig(props));

    /*
     * Start server (for debugging inspection) if database is in-memory
     */
    if (database.startsWith("mem:")) {
      Server server = new Server();
      server.setDatabaseName(0, database.substring(4));
      server.setDatabasePath(0, database);
      server.setPort(9001);
      server.start();
    }

    init(config);
    // postInit(pooledConnection(), structure());
  }

  @Override
  public Translatable.Target target() {
    return HSQLDB;
  }

  @Override
  public Connection rawConnection(int isolationLevel,
                                  String username,
                                  String password) {
    try {
      Connection con = DriverManager.getConnection(
          "jdbc:hsqldb:" + valueOf(config().get(CONFIG_DB_NAME)),
          username,
          password);
      con.setAutoCommit(false);
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
  public Connection pooledConnection(int isolationLevel) {
    try {
//      Connection con = dataSource.getConnection(username, password);
      Connection con = dataSource.getConnection();
      con.setAutoCommit(false);
      if (isolationLevel != -1) {
        con.setTransactionIsolation(isolationLevel);
      }
      return con;
    } catch (Exception e) {
      throw unchecked(e);
    }
  }

  @Override
  public String transactionId(Connection con) {
    try (ResultSet rs = con.createStatement().executeQuery(
      "select cast(current_transaction_id() as varchar)")) {
      rs.next();
      return rs.getString(1);
    } catch(SQLException sqle) {
      throw new RuntimeException(sqle);
    }
  }

  @Override
  public void setArray(PreparedStatement ps, int paramIndex, Object array) throws SQLException {
    Class<?> componentType = array.getClass().getComponentType();
    Type type = Types.typeOf(componentType);
    ps.setArray(paramIndex, ps.getConnection().createArrayOf(type.translate(HSQLDB), (Object[]) array));
  }

  @Override
  public <T> T[] getArray(ResultSet rs, String index, Class<T> componentType) throws SQLException {
    return convert(rs.getArray(index), componentType);
  }

  @Override
  public <T> T[] getArray(ResultSet rs, int index, Class<T> componentType) throws SQLException {
    return convert(rs.getArray(index), componentType);
  }

  private <T> T[] convert(Array sqlArray, Class<T> componentType) throws SQLException {
    if (sqlArray == null) {
      return (T[])java.lang.reflect.Array.newInstance(componentType, 0);
    } else {
      T[] a = (T[])sqlArray.getArray();
      T[] converted = (T[])java.lang.reflect.Array.newInstance(componentType, a.length);
      if (String.class.isAssignableFrom(componentType)) {
        for (int i = 0; i < a.length; i++) {
          if (a[i] == null) {
            converted[i] = null;
          } else {
            converted[i] = (T)((String)a[i]).trim();
          }
        }
      } else {
        System.arraycopy(a, 0, converted, 0, a.length);
      }
      return converted;
    }
  }

  /**
   * Datasource for pooled connections.
   */
  private final HikariDataSource dataSource;
}
