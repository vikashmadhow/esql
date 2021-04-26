/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ma.vi.esql.syntax.Translatable;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.util.Map;
import java.util.Properties;

import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.esql.syntax.Translatable.Target.POSTGRESQL;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Postgresql extends AbstractDatabase {
  public Postgresql(Map<String, Object> config) {
    Properties props = new Properties();
    props.setProperty("dataSourceClassName", PGSimpleDataSource.class.getName());
    props.setProperty("dataSource.serverName", valueOf(config.getOrDefault(CONFIG_DB_HOST, "localhost")));
    if (config.containsKey(CONFIG_DB_PORT)) {
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
    return POSTGRESQL;
  }

  @Override
  public void postInit(Connection con, Structure structure) {
    super.postInit(con, structure);
    try (Connection c = pooledConnection(true, -1)) {
      // Postgresql specific

      // create UUID extension
      c.createStatement().executeUpdate("CREATE SCHEMA IF NOT EXISTS \"" + CORE_SCHEMA + '"');
      c.createStatement().executeUpdate("CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\" SCHEMA pg_catalog");

      // function for getting type (_core.type_name(type_id))
      c.createStatement().executeUpdate(
          "CREATE OR REPLACE FUNCTION \"" + CORE_SCHEMA + "\".type_name(typeid oid) RETURNS TEXT AS $$ \n" +
              "DECLARE\n" +
              "    typename    text;\n" +
              "    namespace   text;\n" +
              "    elementtype int;\n" +
              "    isarray     bool = false;\n" +
              "BEGIN\n" +
              "    SELECT t.typname,\n" +
              "           n.nspname,\n" +
              "           t.typelem INTO typename, namespace, elementtype\n" +
              "    FROM   pg_catalog.pg_type t\n" +
              "    JOIN   pg_catalog.pg_namespace n ON t.typnamespace=n.oid\n" +
              "    WHERE  t.oid=typeid;\n" +
              "\n" +
              "    IF elementtype != 0 THEN\n" +
              "        SELECT t.typname,\n" +
              "               n.nspname,\n" +
              "               t.typelem INTO typename, namespace, elementtype\n" +
              "        FROM   pg_catalog.pg_type t\n" +
              "        JOIN   pg_catalog.pg_namespace n ON t.typnamespace=n.oid\n" +
              "        WHERE  t.oid=elementtype;\n" +
              "        isarray = true;\n" +
              "    END IF;\n" +
              "\n" +
              "    RETURN CASE WHEN namespace IN ('pg_catalog', 'public') THEN typename\n" +
              "                ELSE '\"' || namespace || '\".\"' || typename || '\"'\n" +
              "           END ||\n" +
              "           CASE WHEN isarray THEN '[]' ELSE '' END;\n" +
              "END;\n" +
              "$$ LANGUAGE plpgsql");

       c.createStatement().executeUpdate(
          "create or replace function _core.range(val double precision, variadic intervals bigint[]) returns text as $$\n" +
              "  with range(lb, ub, label) as (\n" +
              "    select intervals[i], intervals[i + 1],\n" +
              "           case i when 0                         then '01. Less than ' || intervals[1]\n" +
              "                  when array_upper(intervals, 1) then lpad((array_upper(intervals, 1) + 1)::text, 2, '0') || '. ' || intervals[i] || ' or more'\n" +
              "                  else                                lpad((i + 1)::text, 2, '0') || '. ' || intervals[i] || ' to ' || (intervals[i+1]-1)\n" +
              "           end\n" +
              "    from generate_series(0, array_upper(intervals, 1)) t(i)\n" +
              "  )\n" +
              "  select label\n" +
              "    from range\n" +
              "   where val >= coalesce(lb, -2000000000)\n" +
              "     and val <  coalesce(ub,  2000000000)\n" +
              "$$ language sql immutable strict;\n"
      );
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
          "jdbc:postgresql://"
              + valueOf(config().get(CONFIG_DB_HOST)) + ':'
              + (config().containsKey(CONFIG_DB_PORT)
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
    ps.setArray(paramIndex, ps.getConnection().createArrayOf(type.translate(POSTGRESQL), (Object[]) array));
  }

  @Override
  public <T> T[] getArray(ResultSet rs, String index, Class<T> componentType) throws SQLException {
    java.sql.Array sqlArray = rs.getArray(index);
    return sqlArray == null
           ? (T[])java.lang.reflect.Array.newInstance(componentType, 0)
           : (T[])sqlArray.getArray();
  }

  @Override
  public <T> T[] getArray(ResultSet rs, int index, Class<T> componentType) throws SQLException {
    java.sql.Array sqlArray = rs.getArray(index);
    return sqlArray == null
           ? (T[])java.lang.reflect.Array.newInstance(componentType, 0)
           : (T[])sqlArray.getArray();
  }

  /**
   * Datasource for pooled connections.
   */
  private final HikariDataSource dataSource;
}
