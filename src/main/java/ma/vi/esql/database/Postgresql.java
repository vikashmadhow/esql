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
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.util.Properties;

import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.esql.translation.Translatable.Target.POSTGRESQL;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Postgresql extends AbstractDatabase {
  public Postgresql(Configuration config) {
    Properties props = new Properties();
    props.setProperty("dataSourceClassName", PGSimpleDataSource.class.getName());
    props.setProperty("dataSource.serverName", valueOf(config.get(CONFIG_DB_HOST, "localhost")));
    if (config.has(CONFIG_DB_PORT)) {
      props.setProperty("dataSource.portNumber", valueOf(config.get(CONFIG_DB_PORT)));
    }
    props.setProperty("dataSource.databaseName", valueOf(config.get(CONFIG_DB_NAME)));
    props.setProperty("dataSource.user", valueOf(config.get(CONFIG_DB_USER)));
    props.setProperty("dataSource.password", valueOf(config.get(CONFIG_DB_PASSWORD)));
    dataSource = new HikariDataSource(new HikariConfig(props));

    init(config);
    // postInit(pooledConnection(), structure());
  }

  @Override
  public Translatable.Target target() {
    return POSTGRESQL;
  }

  @Override
  public void init(Configuration config) {
    super.init(config);
    try (Connection c = pooledConnection()) {
      // Postgresql specific

      // create UUID extension
      // c.createStatement().executeUpdate("CREATE SCHEMA IF NOT EXISTS \"" + CORE_SCHEMA + '"');
      c.createStatement().executeUpdate("CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\" SCHEMA pg_catalog");

      // function for getting type (_core.type_name(type_id))
      c.createStatement().executeUpdate("""
        CREATE OR REPLACE FUNCTION "_core".type_name(typeid oid) RETURNS TEXT AS $$\s
        DECLARE
            typename    text;
            namespace   text;
            elementtype int;
            isarray     bool = false;
        BEGIN
            SELECT t.typname,
                   n.nspname,
                   t.typelem INTO typename, namespace, elementtype
            FROM   pg_catalog.pg_type t
            JOIN   pg_catalog.pg_namespace n ON t.typnamespace=n.oid
            WHERE  t.oid=typeid;
        
            IF elementtype != 0 THEN
                SELECT t.typname,
                       n.nspname,
                       t.typelem INTO typename, namespace, elementtype
                FROM   pg_catalog.pg_type t
                JOIN   pg_catalog.pg_namespace n ON t.typnamespace=n.oid
                WHERE  t.oid=elementtype;
                isarray = true;
            END IF;
        
            RETURN CASE WHEN namespace IN ('pg_catalog', 'public') THEN typename
                        ELSE '"' || namespace || '"."' || typename || '"'
                   END ||
                   CASE WHEN isarray THEN '[]' ELSE '' END;
        END;
        $$ LANGUAGE plpgsql""");

       c.createStatement().executeUpdate("""
         create or replace function _core.range(val double precision, variadic intervals bigint[]) returns text as $$
           with range(lb, ub, label) as (
             select intervals[i], intervals[i + 1],
                    case i when 0                         then '01. Less than ' || intervals[1]
                           when array_upper(intervals, 1) then lpad((array_upper(intervals, 1) + 1)::text, 2, '0') || '. ' || intervals[i] || ' or more'
                           else                                lpad((i + 1)::text, 2, '0') || '. ' || intervals[i] || ' to ' || (intervals[i+1]-1)
                    end
             from generate_series(0, array_upper(intervals, 1)) t(i)
           )
           select label
             from range
            where val >= coalesce(lb, -2000000000)
              and val <  coalesce(ub,  2000000000)
         $$ language sql immutable strict;""");

       /*
        * Coarse-grain event capture trigger functions
        */
       c.createStatement().executeUpdate("""
         create or replace function _core.capture_insert_event() returns trigger as $$
         begin
           if exists(select 1 from inserted) then
             insert into _core._temp_history(trans_id, table_name, event, at_time)
                                values(pg_current_xact_id()::text,
                                       case tg_table_schema
                                         when 'public' then ''
                                         else tg_table_schema || '.'
                                       end || tg_table_name,
                                       'I', now());
           end if;
           return null;
         end;
         $$ language plpgsql""");

       c.createStatement().executeUpdate("""
         create or replace function _core.capture_delete_event() returns trigger as $$
         begin
           if exists(select 1 from deleted) then
             insert into _core._temp_history(trans_id, table_name, event, at_time)
                                values(pg_current_xact_id()::text,
                                       case tg_table_schema
                                         when 'public' then ''
                                         else tg_table_schema || '.'
                                       end || tg_table_name,
                                       'D', now());
           end if;
           return null;
         end;
         $$ language plpgsql""");

       c.createStatement().executeUpdate("""
         create or replace function _core.capture_update_event() returns trigger as $$
         begin
           if exists(select 1 from inserted) then
             insert into _core._temp_history(trans_id, table_name, event, at_time)
                                values(pg_current_xact_id()::text,
                                       case tg_table_schema
                                         when 'public' then ''
                                         else tg_table_schema || '.'
                                       end || tg_table_name,
                                       'U', now());
           end if;
           return null;
         end;
         $$ language plpgsql""");

//      /*
//       * Fine-grain event capture trigger functions
//       */
//      c.createStatement().executeUpdate("""
//         create or replace function _core.capture_insert_history_event() returns trigger as $$
//         begin
//           insert into _core.history
//                  select pg_current_xact_id(),
//                         1, now(), null, inserted.*;
//           return null;
//         end;
//         $$ language plpgsql""");

      c.commit();
    } catch (SQLException e) {
      throw unchecked(e);
    }
  }

  @Override
  public Connection rawConnection(int isolationLevel,
                                  String username,
                                  String password) {
    try {
      Connection con = DriverManager.getConnection(
          "jdbc:postgresql://"
              + valueOf(config().get(CONFIG_DB_HOST)) + ':'
              + (config().has(CONFIG_DB_PORT)
                    ? ':' + valueOf(config().get(CONFIG_DB_PORT))
                    : "")
              + '/' + valueOf(config().get(CONFIG_DB_NAME)),
          username,
          password);
      con.setAutoCommit(false);
      if (isolationLevel != -1 && isolationLevel != SNAPSHOT_ISOLATION) {
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
      if (isolationLevel != -1 && isolationLevel != SNAPSHOT_ISOLATION) {
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
      "select pg_current_xact_id()::text")) {
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
