/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.sql.*;
import java.util.Map;
import java.util.Properties;

import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.esql.parser.Translatable.Target.MYSQL;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class MySql extends AbstractDatabase {
  public MySql(Map<String, Object> config) {
    Properties props = new Properties();
    props.setProperty("dataSourceClassName", MysqlDataSource.class.getName());
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
    return MYSQL;
  }

  @Override
  public void postInit(Connection con, Structure structure) {
    super.postInit(con, structure);
    try (Connection c = pooledConnection(true, -1)) {
      // MySQL specific

      // create UUID extension
      c.createStatement().executeUpdate("CREATE SCHEMA IF NOT EXISTS \"" + CORE_SCHEMA + '"');
//      c.createStatement().executeUpdate("CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\" SCHEMA pg_catalog");

//      // function for getting type (_core.type_name(type_id))
//      c.createStatement().executeUpdate(
//          "CREATE OR REPLACE FUNCTION \"" + CORE_SCHEMA + "\".type_name(typeid oid) RETURNS TEXT AS $$ \n" +
//              "DECLARE\n" +
//              "    typename    text;\n" +
//              "    namespace   text;\n" +
//              "    elementtype int;\n" +
//              "    isarray     bool = false;\n" +
//              "BEGIN\n" +
//              "    SELECT t.typname,\n" +
//              "           n.nspname,\n" +
//              "           t.typelem INTO typename, namespace, elementtype\n" +
//              "    FROM   pg_catalog.pg_type t\n" +
//              "    JOIN   pg_catalog.pg_namespace n ON t.typnamespace=n.oid\n" +
//              "    WHERE  t.oid=typeid;\n" +
//              "\n" +
//              "    IF elementtype != 0 THEN\n" +
//              "        SELECT t.typname,\n" +
//              "               n.nspname,\n" +
//              "               t.typelem INTO typename, namespace, elementtype\n" +
//              "        FROM   pg_catalog.pg_type t\n" +
//              "        JOIN   pg_catalog.pg_namespace n ON t.typnamespace=n.oid\n" +
//              "        WHERE  t.oid=elementtype;\n" +
//              "        isarray = true;\n" +
//              "    END IF;\n" +
//              "\n" +
//              "    RETURN CASE WHEN namespace IN ('pg_catalog', 'public') THEN typename\n" +
//              "                ELSE '\"' || namespace || '\".\"' || typename || '\"'\n" +
//              "           END ||\n" +
//              "           CASE WHEN isarray THEN '[]' ELSE '' END;\n" +
//              "END;\n" +
//              "$$ LANGUAGE plpgsql");
//
//      // @todo: move to platform project
////      // lookup label with no links
////      c.createStatement().executeUpdate("create or replace function _core.lookup_label(code text,\n" +
////                                            "                                              lookup text,\n" +
////                                            "                                              show_code boolean,\n" +
////                                            "                                              show_label boolean) returns text as $$\n" +
////                                            "    select case when coalesce(show_code, false)=coalesce(show_label, false)\n" +
////                                            "                then v.code || ' - ' || v.label\n" +
////                                            "\n" +
////                                            "                when coalesce(show_code, false)=true\n" +
////                                            "                then v.code\n" +
////                                            "\n" +
////                                            "                else v.label\n" +
////                                            "           end\n" +
////                                            "      from \"_platform.lookup\".\"LookupValue\" v\n" +
////                                            "      join \"_platform.lookup\".\"Lookup\" l on v.lookup_id=l._id\n" +
////                                            "     where l.name=$2 and v.code=$1;\n" +
////                                            "$$ language sql immutable;");
////
////      // lookup label with variable number of links
////      c.createStatement().executeUpdate("create or replace function _core.lookup_label(code text,\n" +
////                                            "                                              lookup text,\n" +
////                                            "                                              show_code boolean,\n" +
////                                            "                                              show_label boolean,\n" +
////                                            "                                              variadic links text[]) returns text as $$\n" +
////                                            "declare\n" +
////                                            "    link_name text;\n" +
////                                            "    link_index int = 0;\n" +
////                                            "\n" +
////                                            "    label_clause text = '';\n" +
////                                            "    from_clause text = '';\n" +
////                                            "    query text;\n" +
////                                            "    result text;\n" +
////                                            "\n" +
////                                            "begin\n" +
////                                            "    from_clause := '\"_platform.lookup\".\"LookupValue\" v0 '\n" +
////                                            "                        || 'join \"_platform.lookup\".\"Lookup\" lookup '\n" +
////                                            "                        || 'on (v0.lookup_id=lookup._id and lookup.name=''' || lookup || ''')';\n" +
////                                            "\n" +
////                                            "    foreach link_name in array links loop\n" +
////                                            "        -- source side\n" +
////                                            "        from_clause := from_clause || ' join \"_platform.lookup\".\"LookupValueLink\" lk' || link_index\n" +
////                                            "                                   || ' on (v' || link_index || '._id=lk' || link_index\n" +
////                                            "                                   || '.source_lookup_value_id and ' || 'lk' || link_index\n" +
////                                            "                                   || '.lookup_link=''' || link_name || ''')';\n" +
////                                            "\n" +
////                                            "        link_index := link_index + 1;\n" +
////                                            "\n" +
////                                            "        -- target side\n" +
////                                            "        from_clause := from_clause || ' join \"_platform.lookup\".\"LookupValue\" v' || link_index\n" +
////                                            "                                   || ' on (v' || link_index || '._id=lk' || (link_index - 1)\n" +
////                                            "                                   || '.target_lookup_value_id)';\n" +
////                                            "    end loop;\n" +
////                                            "\n" +
////                                            "    if coalesce(show_code, false)=coalesce(show_label, false) then\n" +
////                                            "        label_clause := 'v' || link_index || '.code || '' - '' || v' || link_index || '.label';\n" +
////                                            "    elsif coalesce(show_code, false)=true then\n" +
////                                            "        label_clause := 'v' || link_index || '.code';\n" +
////                                            "    else\n" +
////                                            "        label_clause := 'v' || link_index || '.label';\n" +
////                                            "    end if;\n" +
////                                            "\n" +
////                                            "    query := 'select ' || label_clause\n" +
////                                            "                       || ' from ' || from_clause\n" +
////                                            "                       || ' where v0.code=''' || code || '''';\n" +
////                                            "\n" +
////                                            "    execute query into result;\n" +
////                                            "    return result;\n" +
////                                            "end;\n" +
////                                            "$$ language plpgsql immutable;\n");
//
//      c.createStatement().executeUpdate(
//          "create or replace function _core.range(val double precision, variadic intervals bigint[]) returns text as $$\n" +
//              "  with range(lb, ub, label) as (\n" +
//              "    select intervals[i], intervals[i + 1],\n" +
//              "           case i when 0                         then '01. Less than ' || intervals[1]\n" +
//              "                  when array_upper(intervals, 1) then lpad((array_upper(intervals, 1) + 1)::text, 2, '0') || '. ' || intervals[i] || ' or more'\n" +
//              "                  else                                lpad((i + 1)::text, 2, '0') || '. ' || intervals[i] || ' to ' || (intervals[i+1]-1)\n" +
//              "           end\n" +
//              "    from generate_series(0, array_upper(intervals, 1)) t(i)\n" +
//              "  )\n" +
//              "  select label\n" +
//              "    from range\n" +
//              "   where val >= coalesce(lb, -2000000000)\n" +
//              "     and val <  coalesce(ub,  2000000000)\n" +
//              "$$ language sql immutable strict;\n"
//      );
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
