/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ma.vi.base.config.Configuration;
import ma.vi.base.string.Escape;
import ma.vi.base.util.Convert;
import ma.vi.esql.translation.Translatable;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.HashSet;
import java.util.Properties;

import static java.util.Collections.emptyMap;
import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.esql.semantic.type.BaseType.BASE_TYPE;
import static ma.vi.esql.translation.Translatable.Target.SQLSERVER;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SqlServer extends AbstractDatabase {
  public SqlServer(Configuration config) {
    Properties props = new Properties();
    props.setProperty("dataSourceClassName", SQLServerConnectionPoolDataSource.class.getName());
    props.setProperty("dataSource.serverName", valueOf(config.get(CONFIG_DB_HOST, "localhost")));
    if (config.has(CONFIG_DB_PORT)) {
      props.setProperty("dataSource.portNumber", valueOf(config.get(CONFIG_DB_PORT)));
    }
    props.setProperty("dataSource.databaseName", valueOf(config.get(CONFIG_DB_NAME)));
    props.setProperty("dataSource.user", valueOf(config.get(CONFIG_DB_USER)));
    props.setProperty("dataSource.password", valueOf(config.get(CONFIG_DB_PASSWORD)));
    props.setProperty("dataSource.sendStringParametersAsUnicode", "true");
    props.setProperty("dataSource.encrypt", "false");
    props.setProperty("dataSource.trustServerCertificate", "true");

    HikariConfig hikariConfig = new HikariConfig(props);

    // Set the desired properties for the Hikari connection pool
    hikariConfig.setMaximumPoolSize(25); // Maximum number of connections in the pool
    hikariConfig.setConnectionTimeout(900000); // Connection timeout in milliseconds (15 minutes)
    hikariConfig.setIdleTimeout(900000); // Idle timeout in milliseconds (15 minutes)
    hikariConfig.setLeakDetectionThreshold(900000); // Leak detection threshold in milliseconds (15 minutes)
    hikariConfig.setMaxLifetime(1800000); // Shorten maxLifetime: Reduce the maxLifetime configuration to ensure connections are refreshed more frequently.
    hikariConfig.setConnectionTestQuery("SELECT 1");
    hikariConfig.setMinimumIdle(5);

    dataSource = new HikariDataSource(hikariConfig);

    init(config);

    /*
     * Load extensions.
     */
    loadExtensions(config.get(CONFIG_DB_EXTENSIONS, emptyMap()), new HashSet<>(), 0);
  }

  @Override
  public Translatable.Target target() {
    return SQLSERVER;
  }

  @Override
  public void init(Configuration config) {
    super.init(config);
    try (Connection c = pooledConnection(-1)) {

      // MS Sql-server specific initialization

      // Allow snapshot isolation
      // c.createStatement().executeUpdate(
      //     "alter database " + valueOf(config().get(CONFIG_DB_NAME)) + " set allow_snapshot_isolation on");

      // function to add intervals to dates
      c.createStatement().executeUpdate(
          """
              create or alter function _core.add_interval(@Date DateTime2, @Interval nvarchar(1000)) returns DateTime2 as
              begin
                declare @IncDate DateTime2;
                declare @IntervalPart nvarchar(20);
                declare @IntervalParts Cursor;
                declare @Indicator nvarchar(2);
                set @IncDate = @Date;
                set @IntervalParts = cursor fast_forward for select * from string_split(@Interval, ',');
                open @IntervalParts;
                fetch next from @IntervalParts into @IntervalPart
                while @@fetch_status = 0
                begin
                  set @IntervalPart = upper(trim(@IntervalPart));
                  set @Indicator = right(@IntervalPart, 1);
                  if @Indicator = 'Y'
                    set @IncDate = dateadd(year, cast(left(@IntervalPart, len(@IntervalPart) - 1) as int), @IncDate)
                  else if @Indicator = 'N'
                    set @IncDate = dateadd(month, cast(left(@IntervalPart, len(@IntervalPart) - 3) as int), @IncDate)
                  else if @Indicator = 'W'
                    set @IncDate = dateadd(week, cast(left(@IntervalPart, len(@IntervalPart) - 1) as int), @IncDate)
                  else if @Indicator = 'D'
                    set @IncDate = dateadd(day, cast(left(@IntervalPart, len(@IntervalPart) - 1) as int), @IncDate)
                  else if @Indicator = 'H'
                    set @IncDate = dateadd(hour, cast(left(@IntervalPart, len(@IntervalPart) - 1) as int), @IncDate)
                  else if @Indicator = 'M'
                    set @IncDate = dateadd(minute, cast(left(@IntervalPart, len(@IntervalPart) - 1) as int), @IncDate)
                  else if @Indicator = 'S'
                  begin
                    if right(@IntervalPart, 2) = 'MS'
                      set @IncDate = dateadd(millisecond, cast(left(@IntervalPart, len(@IntervalPart) - 2) as int), @IncDate)
                    else
                      set @IncDate = dateadd(second, cast(left(@IntervalPart, len(@IntervalPart) - 1) as int), @IncDate)
                  end;
                  fetch next from @IntervalParts into @IntervalPart;
                end;
                close @intervalparts;
                deallocate @intervalparts;
                return @incdate;
              end;""");

      // function to combine two intervals
      c.createStatement().executeUpdate(
          """
              create or alter function _core.add_intervals(@Interval1 nvarchar(1000), @Interval2 nvarchar(1000)) returns nvarchar(1000) as
              begin
                declare @Years   Int = 0;
                declare @Months  Int = 0;
                declare @Days    Int = 0;
                declare @Weeks   Int = 0;
                declare @Hours   Int = 0;
                declare @Minutes Int = 0;
                declare @Seconds Int = 0;
                declare @Millis  Int = 0;
                declare @Interval nvarchar(1000);
                declare @IntervalPart nvarchar(20);
                declare @IntervalParts Cursor;
                declare @IntervalsCursor Cursor;
                declare @Indicator nvarchar(2);
                declare @Intervals table (value nvarchar(1000));
                insert into @Intervals values (@Interval1), (@Interval2);
                set @IntervalsCursor = cursor fast_forward for select value from @Intervals;
                open @IntervalsCursor;
                fetch next from @IntervalsCursor into @Interval;
                while @@fetch_status = 0
                begin
                    set @IntervalParts = Cursor fast_forward for select * from string_split(@Interval, ',');
                    open @IntervalParts;
                    fetch next from @IntervalParts into @IntervalPart
                    while @@fetch_status = 0
                    begin
                      set @IntervalPart = upper(trim(@IntervalPart));
                      set @Indicator = right(@IntervalPart, 1);
                      if @Indicator = 'Y'
                        set @Years = @Years + cast(left(@IntervalPart, len(@IntervalPart) - 1) as int)
                      else if @Indicator = 'N'
                        set @Months = @Months + cast(left(@IntervalPart, len(@IntervalPart) - 3) as int)
                      else if @Indicator = 'W'
                        set @Weeks = @Weeks + cast(left(@IntervalPart, len(@IntervalPart) - 1) as int)
                      else if @Indicator = 'D'
                        set @Days = @Days + cast(left(@IntervalPart, len(@IntervalPart) - 1) as int)
                      else if @Indicator = 'H'
                        set @Hours = @Hours + cast(left(@IntervalPart, len(@IntervalPart) - 1) as int)
                      else if @Indicator = 'M'
                        set @Minutes = @Minutes + cast(left(@IntervalPart, len(@IntervalPart) - 1) as int)
                      else if @Indicator = 'S'
                      begin
                        if right(@IntervalPart, 2) = 'MS'
                          set @Millis = @Millis + cast(left(@IntervalPart, len(@IntervalPart) - 2) as int)
                        else
                          set @Seconds = @Seconds + cast(left(@IntervalPart, len(@IntervalPart) - 1) as int)
                      end;
                      fetch next from @IntervalParts into @IntervalPart;
                    end;
                    close @intervalparts;
                    deallocate @intervalparts;
                    fetch next from @IntervalsCursor into @Interval
                end
                close @IntervalsCursor;
                deallocate @IntervalsCursor;
                set @Interval = '';
                if @Years != 0
                  set @InterVal = @Interval + cast(@Years as nvarchar) + 'y';
                if @Months != 0
                  if len(@Interval) > 0
                    set @Interval = @Interval + ','
                  set @InterVal = @Interval + cast(@Months as nvarchar) + 'mon';
                if @Days != 0
                  if len(@Interval) > 0
                    set @Interval = @Interval + ','
                  set @InterVal = @Interval + cast(@Days as nvarchar) + 'd';
                if @Weeks != 0
                  if len(@Interval) > 0
                    set @Interval = @Interval + ','
                  set @InterVal = @Interval + cast(@Weeks as nvarchar) + 'w';
                if @Hours != 0
                  if len(@Interval) > 0
                    set @Interval = @Interval + ','
                  set @InterVal = @Interval + cast(@Hours as nvarchar) + 'h';
                if @Minutes != 0
                  if len(@Interval) > 0
                    set @Interval = @Interval + ','
                  set @InterVal = @Interval + cast(@Minutes as nvarchar) + 'm';
                if @Seconds != 0
                  if len(@Interval) > 0
                    set @Interval = @Interval + ','
                  set @InterVal = @Interval + cast(@Seconds as nvarchar) + 's';
                if @Millis != 0
                  if len(@Interval) > 0
                    set @Interval = @Interval + ','
                  set @InterVal = @Interval + cast(@Millis as nvarchar) + 'ms';
                return @Interval;
              end;
              """);

      c.createStatement().executeUpdate("""
          create or alter function _core.rpad(@Str nvarchar(max), @Length int, @Pad nvarchar=' ') returns nvarchar(max)
          begin
            declare @Remaining int;
            if len(@Str) < @Length
            begin
              set @Remaining = @Length - len(@Str);
              set @Str = @Str + left(replicate(@Pad, @Remaining), @Remaining);
            end;
            return @Str;
          end;""");

      c.createStatement().executeUpdate("""
          create or alter function _core.lpad(@Str nvarchar(max), @Length int, @Pad nvarchar=' ') returns nvarchar(max)
          begin
            declare @Remaining int;
            if len(@Str) < @Length
            begin
              set @Remaining = @Length - len(@Str);
              set @Str =  left(replicate(@Pad, @Remaining), @Remaining) + @Str;
            end;
            return @Str;
          end;""");

      c.createStatement().executeUpdate("""
          create or alter function _core.range(@Val float, @Intervals nvarchar(max)) returns nvarchar(max) as
          begin
            declare @Result nvarchar(max);

            --  Holds  ranges from each entry in interval.
            declare @Range table(ord bigint, lb bigint, ub bigint, label nvarchar(1000));

            -- E.g. 10,15,30 becomes 10-15, 15-30, 30-
            insert into @Range(ord, lb, ub)
              select 1 + row_number() over(order by cast(value as bigint)),
                     cast(value as bigint) lb,
                     lead(cast(value as bigint), 1, null) over(order by cast(value as bigint))
                from string_split(@Intervals, ',');

            -- Add label for intermediate ranges (e.g. 10-15, 15-30)
            update @Range
               set label=_core.lpad(cast(ord as nvarchar), 2, '0') + '. ' + cast(lb as nvarchar(100)) + ' to ' + cast(ub-1 as nvarchar(100))
             where ub is not null;

            -- Add end open range (e.g 30 or more)
            update @Range
               set label=_core.lpad(cast(ord as nvarchar), 2, '0') + '. ' + cast(lb as nvarchar(100)) + ' or more',
                   ub=9999999999999999
             where ub is null;

            -- Add start open range (< min)
            insert into @Range(lb, ub, label)
            select -9999999999999999, min(lb), '01. Less than ' + cast(min(lb) as nvarchar(max))
              from @Range

            select @Result=label
              from @Range
             where @Val >= lb and @Val < ub;

            return @Result;
          end;""");

      c.createStatement().executeUpdate(
          """
          create or alter function _core.floormod(@dividend Int, @divider Int) returns Int as
          begin
              return ((@dividend % @divider) + @divider) % @divider;
          end;""");

      c.createStatement().executeUpdate(
          """
          create or alter function _core.obfuscate_shift(@i Int) returns Int as
          begin
            if @i % 2 = 0
              set @i = @i + 3
            else
              set @i = -(@i + 3);
            return @i
          end;""");

      c.createStatement().executeUpdate(
          """
          create or alter function _core.unobfuscate(@obfuscated nvarchar(1000)) returns nvarchar(1000) as
          begin
            declare @unobfuscated    nvarchar(1000) = '';
            declare @i               Int = 2;
            declare @pos             Int;
            declare @c               Char;
            declare @password_chars  nvarchar(100) = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
            declare @password_chars2 nvarchar(100) = '~`!@#$%^&*()_-=+[]{}|?<>.,:;\\/';
            declare @chars_len       Int = len(@password_chars);
            declare @chars2_len      Int = len(@password_chars2);

            while @i <= len(@obfuscated)
            begin
                set @c = substring(@obfuscated, @i, 1);
                set @pos = charindex(@c, @password_chars collate Latin1_General_CS_AS);
                
                if @pos > 0
                begin
                  set @pos = @pos - _core.obfuscate_shift(cast((@i - 1) / 2 as Int)) - 1;
                  set @unobfuscated = @unobfuscated + substring(@password_chars, _core.floormod(@pos, @chars_len) + 1, 1);
                end;
                else
                begin
                  set @pos = charindex(@c, @password_chars2 collate Latin1_General_CS_AS);
                  if @pos > 0
                  begin
                    set @pos = @pos - _core.obfuscate_shift(cast((@i - 1) / 2 as Int)) - 1;
                    set @unobfuscated = @unobfuscated + substring(@password_chars2, _core.floormod(@pos, @chars2_len) + 1, 1);
                  end;
                  else
                    set @unobfuscated = @unobfuscated + @c;
                end;
                set @i = @i + 2;
            end
            return @unobfuscated;
          end;""");

      //
      // USE [aletia2]
      // GO
      // CREATE VIEW [_core].[RandomSeedView] AS
      // SELECT ABS(CHECKSUM(NEWID())) AS Seed
      // GO
      c.createStatement().executeUpdate(
          """
          create or alter function _core.randomstr(@length Int) returns nvarchar(1000) as
          begin
            declare @str            nvarchar(1000) = '';
            declare @i              Int = 1;
            declare @password_chars nvarchar(100) = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
            declare @chars_len      Int = len(@password_chars);
            declare @random         BigInt = (select Seed from _core.RandomSeedView);
            declare @a              BigInt = 1664525;
            declare @c              BigInt = 1013904223;
            declare @m              BigInt = power(cast(2 as bigint), 32);

            while @i <= @length
            begin
                set @random = (@a * @random + @c) % @m;
                set @str = @str + substring(@password_chars, @random % @chars_len + 1, 1);
                set @i = @i + 1;
            end
            return @str;
          end;""");

      c.createStatement().executeUpdate(
          """
          create or alter function _core.obfuscate(@unobfuscated nvarchar(1000)) returns nvarchar(1000) as
          begin
            declare @obfuscated      nvarchar(1000) = '';
            declare @i               Int = 1;
            declare @pos             Int;
            declare @ch              Char;
            declare @password_chars  nvarchar(100) = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
            declare @password_chars2 nvarchar(100) = '~`!@#$%^&*()_-=+[]{}|?<>.,:;\\/';
            declare @chars_len       Int = len(@password_chars);
            declare @chars2_len      Int = len(@password_chars2);
            declare @random          BigInt = datepart(millisecond, getutcdate());
            declare @a               BigInt = 1664525;
            declare @c               BigInt = 1013904223;
            declare @m               BigInt = power(cast(2 as bigint), 32);

            while @i <= len(@unobfuscated)
            begin
              set @ch = substring(@unobfuscated, @i, 1);
              set @pos = charindex(@ch, @password_chars collate Latin1_General_CS_AS);
              if @pos > 0
              begin
                set @pos = @pos + _core.obfuscate_shift(@i - 1) - 1;
                set @random = (@a * @random + @c) % @m;
                set @obfuscated = @obfuscated + substring(@password_chars, @random % @chars_len + 1, 1);
                set @obfuscated = @obfuscated + substring(@password_chars, _core.floormod(@pos, @chars_len) + 1, 1);
              end;
              else
              begin
                set @pos = charindex(@ch, @password_chars2 collate Latin1_General_CS_AS);
                if @pos > 0
                begin
                  set @pos = @pos + _core.obfuscate_shift(@i - 1) - 1;
                  set @random = (@a * @random + @c) % @m;
                  set @obfuscated = @obfuscated + substring(@password_chars, @random % @chars_len + 1, 1);
                  set @obfuscated = @obfuscated + substring(@password_chars2, _core.floormod(@pos, @chars2_len) + 1, 1);
                end;
                else
                  set @obfuscated = @obfuscated + @ch;
              end;
              
              set @i = @i + 1;
            end
            return @obfuscated;
          end;""");

      c.createStatement().executeUpdate(
        """
        create or alter function _core.checkdigit(@value bigint) returns bigint as
        begin
          declare @mul    bigint = 3;
          declare @sum    bigint = 0;
          declare @val    bigint = @value;
          declare @mod    bigint;
          declare @cdigit bigint;
          while @val > 0
          begin
            set @mod = @val % 10;
            set @sum = @sum + @mod * @mul;
            if @mul = 3
              set @mul = 1;
            else
              set @mul = 3;
            set @val = floor(@val / 10);
          end;
          set @cdigit = 10 - (@sum % 10);
          if @cdigit = 10
            set @cdigit = 0;
          return @value * 10 + @cdigit;
        end;""");

      c.createStatement().executeUpdate(
        """
        create or alter function _core.hash256(@value nvarchar(1000)) returns nvarchar(1000) as
        begin
          return convert(varchar(max), HASHBYTES('SHA2_256', @value), 2);
        end;""");

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
      StringBuilder url = new StringBuilder("jdbc:sqlserver://");
      if (config().has(CONFIG_DB_HOST)) {
        url.append(valueOf(config().get(CONFIG_DB_HOST)));
        if (config().has(CONFIG_DB_PORT)) {
          url.append(":").append(valueOf(config().get(CONFIG_DB_PORT)));
        }
      }
      url.append(";database=").append(valueOf(config().get(CONFIG_DB_NAME)));
      url.append(";sendStringParametersAsUnicode=true");
      Connection con = DriverManager.getConnection(url.toString(),
                                                   username,
                                                   password);
      if (isolationLevel == SNAPSHOT_ISOLATION) {
        con.setTransactionIsolation(SQLServerConnection.TRANSACTION_SNAPSHOT);
      } else if (isolationLevel != -1) {
        con.setTransactionIsolation(isolationLevel);
      }
      con.setAutoCommit(false);
      con.createStatement().executeUpdate("begin transaction");
      return con;
    } catch (Exception e) {
      throw unchecked(e);
    }
  }

  @Override
  public Connection pooledConnection(int isolationLevel) {
    try {
      Connection con = dataSource.getConnection();
      if (isolationLevel == SNAPSHOT_ISOLATION) {
        con.setTransactionIsolation(SQLServerConnection.TRANSACTION_SNAPSHOT);
      } else if (isolationLevel != -1) {
        con.setTransactionIsolation(isolationLevel);
      }
      con.setAutoCommit(false);
      con.createStatement().executeUpdate("begin transaction");
      return con;
    } catch (Exception e) {
      throw unchecked(e);
    }
  }

  @Override
  public void createSchema(Connection con, String schema) {
    if (schema != null) {
      try (ResultSet rs = con.createStatement().executeQuery(
        "select name from sys.schemas where name='" + schema + "'")) {
        if (!rs.next()) {
          con.createStatement().executeUpdate("create schema \"" + schema + '"');
        }
      } catch(SQLException sqle) {
        throw new RuntimeException(sqle);
      }
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
  public void setArray(PreparedStatement ps,
                       int paramIndex,
                       Object array) throws SQLException {
    _setArray(ps, paramIndex, array);
  }

  protected static void _setArray(PreparedStatement ps,
                                  int paramIndex,
                                  Object array) throws SQLException {
    StringBuilder st = new StringBuilder();
    int length = Array.getLength(array);
    boolean first = true;
    for (int i = 0; i < length; i++) {
      if (first) {
        first = false;
      } else {
        st.append(',');
      }
      Object value = Array.get(array, i);
      st.append(value == null ? "" : String.valueOf(value));
    }
    ps.setString(paramIndex, st.toString());
  }

  @Override
  public <T> T[] getArray(ResultSet rs,
                          String index,
                          Class<T> componentType) throws SQLException {
    return arrayOf(rs.getString(index), componentType);
  }

  @Override
  public <T> T[] getArray(ResultSet rs,
                          int index,
                          Class<T> componentType) throws SQLException {
    return arrayOf(rs.getString(index), componentType);
  }

  protected static <T> T[] arrayOf(String array,
                                   Class<T> componentType) {
    if (array == null) {
      return null;

    } else if (array.trim().length() == 0) {
      return (T[])Array.newInstance(componentType, 0);

    } else {
      /*
       * Arrays can be 3 different forms: a|b|c,... or [a|b|c|...] or [a|b|c...]<type>.
       * For the latter two cases, extract the internal array contents before parsing.
       */
      String remapped = ARRAY_ESCAPE.map(array);
      if (remapped.charAt(0) == '[') {
        if (remapped.charAt(array.length() - 1) == ']') {
          remapped = remapped.substring(1, array.length() - 1);
        } else {
          int pos = remapped.lastIndexOf(']');
          if (pos != -1) {
            /*
             * check if array end is prefixed with a base type; * remove if so.
             */
            String suffix = remapped.substring(pos + 1);
            if (BASE_TYPE.matcher(suffix).matches()) {
              remapped = remapped.substring(0, pos);
            }
          }
        }
      }
      if (remapped.trim().length() == 0) {
        return (T[])Array.newInstance(componentType, 0);
      } else {
        String[] split = remapped.split("\\|");
        T[] list = (T[])Array.newInstance(componentType, split.length);
        for (int i = 0; i < split.length; i++) {
          list[i] = (T)Convert.convert(ARRAY_ESCAPE.demap(split[i]), componentType);
        }
        return list;
      }
    }
  }

  public static final Escape ARRAY_ESCAPE = new Escape("\\|[]");

  /**
   * Datasource for pooled connections.
   */
  private final HikariDataSource dataSource;
}
