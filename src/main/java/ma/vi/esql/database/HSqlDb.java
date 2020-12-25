/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;
import org.hsqldb.Server;
import org.hsqldb.jdbc.JDBCDataSource;

import java.sql.*;
import java.util.Map;
import java.util.Properties;

import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.esql.parser.Translatable.Target.HSQLDB;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class HSqlDb extends AbstractDatabase {
  public HSqlDb(Map<String, Object> config,
                boolean createCoreTables,
                boolean createPlatformTables) {
    this.config = config;

    Properties props = new Properties();
    props.setProperty("dataSourceClassName", JDBCDataSource.class.getName());
    if (config.containsKey("database.host")) {
      props.setProperty("dataSource.serverName", valueOf(config.get("database.host")));
    }
    if (config.containsKey("database.port")) {
      props.setProperty("dataSource.portNumber", valueOf(config.get("database.port")));
    }
    String database = valueOf(config.get("database.name"));
    props.setProperty("dataSource.databaseName", database);
    if (config.get("database.user.name") != null
     && config.get("database.user.password") != null) {
      props.setProperty("dataSource.user", valueOf(config.get("database.user.name")));
      props.setProperty("dataSource.password", valueOf(config.get("database.user.password")));
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
    postInit(pooledConnection(), structure(), createCoreTables, createPlatformTables);
  }

  @Override
  public Map<String, Object> config() {
    return config;
  }

  @Override
  public Translatable.Target target() {
    return HSQLDB;
  }

  @Override
  public void postInit(Connection con,
                       Structure structure,
                       boolean createCoreTables,
                       boolean createPlatformTables) {
    super.postInit(con, structure, createCoreTables, createPlatformTables);
    try (Connection c = pooledConnection(true, -1)) {
      // HSqlDB specific
      // c.createStatement().executeUpdate("CREATE SCHEMA IF NOT EXISTS \"" + CORE_SCHEMA + '"');

      // lookup label with no links
      /*
      c.createStatement().executeUpdate("create or replace function _core.lookup_label(code text,\n" +
                                            "                                              lookup text,\n" +
                                            "                                              show_code boolean,\n" +
                                            "                                              show_label boolean) returns text as $$\n" +
                                            "    select case when coalesce(show_code, false)=coalesce(show_label, false)\n" +
                                            "                then v.code || ' - ' || v.label\n" +
                                            "\n" +
                                            "                when coalesce(show_code, false)=true\n" +
                                            "                then v.code\n" +
                                            "\n" +
                                            "                else v.label\n" +
                                            "           end\n" +
                                            "      from \"_platform.lookup\".\"LookupValue\" v\n" +
                                            "      join \"_platform.lookup\".\"Lookup\" l on v.lookup_id=l._id\n" +
                                            "     where l.name=$2 and v.code=$1;\n" +
                                            "$$ language sql immutable;");

      // lookup label with variable number of links
      c.createStatement().executeUpdate("create or replace function _core.lookup_label(code text,\n" +
                                            "                                              lookup text,\n" +
                                            "                                              show_code boolean,\n" +
                                            "                                              show_label boolean,\n" +
                                            "                                              variadic links text[]) returns text as $$\n" +
                                            "declare\n" +
                                            "    link_name text;\n" +
                                            "    link_index int = 0;\n" +
                                            "\n" +
                                            "    label_clause text = '';\n" +
                                            "    from_clause text = '';\n" +
                                            "    query text;\n" +
                                            "    result text;\n" +
                                            "\n" +
                                            "begin\n" +
                                            "    from_clause := '\"_platform.lookup\".\"LookupValue\" v0 '\n" +
                                            "                        || 'join \"_platform.lookup\".\"Lookup\" lookup '\n" +
                                            "                        || 'on (v0.lookup_id=lookup._id and lookup.name=''' || lookup || ''')';\n" +
                                            "\n" +
                                            "    foreach link_name in array links loop\n" +
                                            "        -- source side\n" +
                                            "        from_clause := from_clause || ' join \"_platform.lookup\".\"LookupValueLink\" lk' || link_index\n" +
                                            "                                   || ' on (v' || link_index || '._id=lk' || link_index\n" +
                                            "                                   || '.source_lookup_value_id and ' || 'lk' || link_index\n" +
                                            "                                   || '.lookup_link=''' || link_name || ''')';\n" +
                                            "\n" +
                                            "        link_index := link_index + 1;\n" +
                                            "\n" +
                                            "        -- target side\n" +
                                            "        from_clause := from_clause || ' join \"_platform.lookup\".\"LookupValue\" v' || link_index\n" +
                                            "                                   || ' on (v' || link_index || '._id=lk' || (link_index - 1)\n" +
                                            "                                   || '.target_lookup_value_id)';\n" +
                                            "    end loop;\n" +
                                            "\n" +
                                            "    if coalesce(show_code, false)=coalesce(show_label, false) then\n" +
                                            "        label_clause := 'v' || link_index || '.code || '' - '' || v' || link_index || '.label';\n" +
                                            "    elsif coalesce(show_code, false)=true then\n" +
                                            "        label_clause := 'v' || link_index || '.code';\n" +
                                            "    else\n" +
                                            "        label_clause := 'v' || link_index || '.label';\n" +
                                            "    end if;\n" +
                                            "\n" +
                                            "    query := 'select ' || label_clause\n" +
                                            "                       || ' from ' || from_clause\n" +
                                            "                       || ' where v0.code=''' || code || '''';\n" +
                                            "\n" +
                                            "    execute query into result;\n" +
                                            "    return result;\n" +
                                            "end;\n" +
                                            "$$ language plpgsql immutable;\n");

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
      */
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
          "jdbc:hsqldb:" + valueOf(config.get("database.name")),
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
      System.arraycopy(a, 0, converted, 0, a.length);
      return converted;
    }
  }

  /**
   * Datasource for pooled connections.
   */
  private final HikariDataSource dataSource;

  private final Map<String, Object> config;
}
