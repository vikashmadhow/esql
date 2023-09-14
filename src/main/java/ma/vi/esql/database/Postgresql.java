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
import java.util.HashSet;
import java.util.Properties;

import static java.util.Collections.emptyMap;
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

    /*
     * Load extensions.
     */
    loadExtensions(config.get(CONFIG_DB_EXTENSIONS, emptyMap()), new HashSet<>(), 0);
  }

  @Override
  public Translatable.Target target() {
    return POSTGRESQL;
  }

  @Override
  public void init(Configuration config) {
    super.init(config);
    try (Connection c = pooledConnection()) {
      /*
       * Postgresql specific.
       */

      // create UUID extension
      // c.createStatement().executeUpdate("CREATE SCHEMA IF NOT EXISTS \"" + CORE_SCHEMA + '"');
      c.createStatement().executeUpdate("CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\" SCHEMA pg_catalog");

      /*
       * trycast emulation.
       */
      c.createStatement().executeUpdate("""
       CREATE OR REPLACE FUNCTION _core._try_cast(_in text, INOUT _out ANYELEMENT) LANGUAGE plpgsql AS
        $$
        BEGIN
           EXECUTE format('SELECT %L::%s', $1, pg_typeof(_out)) INTO _out;
        EXCEPTION WHEN others THEN
           -- do nothing: _out carries default
        END
        $$;""");

      /*
       * function for getting type (_core.type_name(type_id))
       */
      c.createStatement().executeUpdate("""
        CREATE OR REPLACE FUNCTION "_core".type_name(typeid oid) RETURNS TEXT AS $$
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

      c.createStatement().executeUpdate(
          """
          create or replace function _core.floormod(dividend int, divider int) returns int as $$
            select ((dividend % divider) + divider) % divider;
          $$ language sql immutable strict""");

      c.createStatement().executeUpdate(
          """
          create or replace function _core.obfuscate_shift(i int) returns int as $$
            select case when i % 2 = 0
                        then i + 3
                        else -(i + 3) end;
          $$ language sql immutable strict""");

      c.createStatement().executeUpdate(
          """
          create or replace function _core.unobfuscate(obfuscated text) returns text as $$
          declare
             unobfuscated    text := '';
             i               int  := 2;
             pos             int;
             ch              char;
             password_chars  text := 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
             password_chars2 text := '~`!@#$%^&*()_-=+[]{}|?<>.,:;\\/';
             chars_len       int  := length(password_chars);
             chars2_len      int  := length(password_chars2);
          begin
            while i <= length(obfuscated) loop
              ch  := substr(obfuscated, i, 1);
              pos := strpos(password_chars, ch);
              if pos > 0 then
                pos := pos - _core.obfuscate_shift(cast((i - 1) / 2 as int)) - 1;
                unobfuscated := unobfuscated || substr(password_chars, _core.floormod(pos, chars_len)::int + 1, 1);
              else
                pos := strpos(password_chars2, ch);
                if pos > 0 then
                  pos := strpos(password_chars2, ch) - _core.obfuscate_shift(cast((i - 1) / 2 as int)) - 1;
                  unobfuscated := unobfuscated || substr(password_chars2, _core.floormod(pos, chars2_len)::int + 1, 1);
                else
                  unobfuscated := unobfuscated || ch;
                end if;
              end if;
              
              i := i + 2;
            end loop;
            return unobfuscated;
          end;
          $$ language plpgsql immutable strict""");

      c.createStatement().executeUpdate(
          """
          create or replace function _core.randomstr(length int) returns text as $$
          declare
            str            text   := '';
            i              int    := 1;
            password_chars text   := 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
            chars_len      int    := length(password_chars);
            random         bigint := date_part('milliseconds', now());
            a              bigint := 1664525;
            c              bigint := 1013904223;
            m              bigint := power(cast(2 as bigint), 32);
          begin
            while i <= length loop
              random := (a * random + c) % m;
              str    := str + substr(password_chars, random % chars_len + 1, 1);
              i      := i + 1;
            end loop;
            return str;
          end;
          $$ language plpgsql immutable strict""");

      c.createStatement().executeUpdate(
          """
          create or replace function _core.obfuscate(unobfuscated text) returns text as $$
          declare
            obfuscated      text := '';
            i               int  := 1;
            pos             int;
            ch              char;
            password_chars  text   := 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
            password_chars2 text   := '~`!@#$%^&*()_-=+[]{}|?<>.,:;\\/';
            chars_len       int    := length(password_chars);
            chars2_len      int    := length(password_chars2);
            random          bigint := date_part('milliseconds', now());
            a               bigint := 1664525;
            c               bigint := 1013904223;
            m               bigint := power(cast(2 as bigint), 32);
          begin
            while i <= length(unobfuscated) loop
              ch  := substr(unobfuscated, i, 1);
              pos := strpos(password_chars, ch);
              if pos > 0 then
                pos        := pos + _core.obfuscate_shift(i - 1) - 1;
                random     := (a * random + c) % m;
                obfuscated := obfuscated || substr(password_chars, (random % chars_len + 1)::int, 1);
                obfuscated := obfuscated || substr(password_chars, _core.floormod(pos, chars_len) + 1, 1);
              else
                pos := strpos(password_chars2, ch);
                if pos > 0 then
                  pos        := pos + _core.obfuscate_shift(i - 1) - 1;
                  random     := (a * random + c) % m;
                  obfuscated := obfuscated || substr(password_chars,  (random % chars_len + 1)::int, 1);
                  obfuscated := obfuscated || substr(password_chars2, _core.floormod(pos, chars2_len) + 1, 1);
                else
                  obfuscated := obfuscated || ch;
                end if;
              end if;

              i := i + 1;
            end loop;
            return obfuscated;
          end;
          $$ language plpgsql immutable strict""");

      c.createStatement().executeUpdate(
          """
          create or replace function _core.checkdigit(value bigint) returns bigint as $$
          declare
            mul    bigint := 3;
            sum    bigint := 0;
            val    bigint := value;
            mod    bigint;
            cdigit bigint;
          begin
            while val > 0 loop
              mod := val % 10;
              sum := sum + mod * mul;
              if mul = 3 then
                mul := 1;
              else
                mul := 3;
              end if;
              val := floor(val / 10);
            end loop;
            cdigit = 10 - (sum % 10);
            if cdigit = 10 then
              cdigit := 0;
            end if;
            return value * 10 + cdigit;
          end;
          $$ language plpgsql immutable strict""");

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
      StringBuilder url = new StringBuilder("jdbc:postgresql:");
      if (config().has(CONFIG_DB_HOST)) {
        url.append("//").append(valueOf(config().get(CONFIG_DB_HOST)));
        if (config().has(CONFIG_DB_PORT)) {
          url.append(":").append(valueOf(config().get(CONFIG_DB_PORT)));
        }
        url.append('/');
      }
      url.append(valueOf(config().get(CONFIG_DB_NAME)));
      Connection con = DriverManager.getConnection(url.toString(),
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
  public void createSchema(Connection con, String schema) {
    if (schema != null) {
      try {
        con.createStatement().executeUpdate("create schema if not exists \"" + schema + '"');
      } catch(SQLException sqle) {
        throw new RuntimeException(sqle);
      }
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
