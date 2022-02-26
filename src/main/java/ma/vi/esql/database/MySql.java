/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ma.vi.base.config.Configuration;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.translation.Translatable;

import java.sql.*;
import java.util.Properties;

import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.esql.translation.Translatable.Target.MYSQL;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class MySql extends AbstractDatabase {
  public MySql(Configuration config) {
    Properties props = new Properties();
    props.setProperty("dataSourceClassName", MysqlDataSource.class.getName());
    props.setProperty("dataSource.serverName", valueOf(config.get(CONFIG_DB_HOST, "localhost")));
    if (config.has(CONFIG_DB_PORT)) {
      props.setProperty("dataSource.portNumber", valueOf(config.get(CONFIG_DB_PORT)));
    }
    props.setProperty("dataSource.databaseName", valueOf(config.get(CONFIG_DB_NAME)));
    props.setProperty("dataSource.user", valueOf(config.get(CONFIG_DB_USER)));
    props.setProperty("dataSource.password", valueOf(config.get(CONFIG_DB_PASSWORD)));
    dataSource = new HikariDataSource(new HikariConfig(props));

    init(config);
    postInit(pooledConnection(), structure());
  }

  @Override
  public Translatable.Target target() {
    return MYSQL;
  }

  @Override
  public void postInit(Connection con, Structure structure) {
    super.postInit(con, structure);
    try (Connection c = pooledConnection(true, -1)) {
      // MySQL specific
      c.createStatement().executeUpdate("CREATE SCHEMA IF NOT EXISTS \"" + CORE_SCHEMA + '"');
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
      Connection con = DriverManager.getConnection(
          "jdbc:mysql://"
              + valueOf(config().get(CONFIG_DB_HOST)) + ':'
              + (config().has(CONFIG_DB_PORT)
                    ? ':' + valueOf(config().get(CONFIG_DB_PORT))
                    : "")
              + '/' + valueOf(config().get(CONFIG_DB_NAME)),
          username,
          password);
      con.setAutoCommit(autoCommit);
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
    Class<?> componentType = array.getClass().getComponentType();
    Type type = Types.typeOf(componentType);
    ps.setArray(paramIndex, ps.getConnection().createArrayOf(type.translate(MYSQL), (Object[]) array));
  }

  @Override
  public <T> T[] getArray(ResultSet rs, String index, Class<T> componentType) throws SQLException {
    Array sqlArray = rs.getArray(index);
    return sqlArray == null
           ? (T[])java.lang.reflect.Array.newInstance(componentType, 0)
           : (T[])sqlArray.getArray();
  }

  @Override
  public <T> T[] getArray(ResultSet rs, int index, Class<T> componentType) throws SQLException {
    Array sqlArray = rs.getArray(index);
    return sqlArray == null
           ? (T[])java.lang.reflect.Array.newInstance(componentType, 0)
           : (T[])sqlArray.getArray();
  }

  /**
   * Datasource for pooled connections.
   */
  private final HikariDataSource dataSource;
}
