/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ma.vi.base.util.Convert;
import ma.vi.esql.parser.Translatable;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.Map;
import java.util.Properties;

import static ma.vi.base.collections.ArrayUtils.ARRAY_ESCAPE;
import static ma.vi.base.lang.Errors.unchecked;
import static ma.vi.esql.parser.Translatable.Target.SQLSERVER;
import static ma.vi.esql.type.BaseType.BASE_TYPE;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SqlServer extends AbstractDatabase {
  public SqlServer(Map<String, Object> config,
                   boolean createCoreTables,
                   boolean createPlatformTables) {
    this.config = config;

    Properties props = new Properties();
    props.setProperty("dataSourceClassName", SQLServerConnectionPoolDataSource.class.getName());
    props.setProperty("dataSource.serverName", valueOf(config.getOrDefault("database.host", "localhost")));
    if (config.containsKey("database.port")) {
      props.setProperty("dataSource.portNumber", valueOf(config.get("database.port")));
    }
    props.setProperty("dataSource.databaseName", valueOf(config.get("database.name")));
    props.setProperty("dataSource.user", valueOf(config.get("database.user.name")));
    props.setProperty("dataSource.password", valueOf(config.get("database.user.password")));
    props.setProperty("dataSource.sendStringParametersAsUnicode", "true");
    dataSource = new HikariDataSource(new HikariConfig(props));

    init(config);
    postInit(pooledConnection(), structure(), createCoreTables, createPlatformTables);
  }

  @Override
  public void init(Map<String, Object> config) {
    structure();
  }

  @Override
  public Map<String, Object> config() {
    return config;
  }

  @Override
  public Translatable.Target target() {
    return SQLSERVER;
  }

  @Override
  public void postInit(Connection con,
                       Structure structure,
                       boolean createCoreTables,
                       boolean createPlatformTables) {
    super.postInit(con, structure, createCoreTables, createPlatformTables);
    try (Connection c = pooledConnection(true, -1)) {

      // MS Sql-server specific initialization

      // Allow snapshot isolation
      c.createStatement().executeUpdate(
          "alter database " + valueOf(config.get("database.name")) + " set allow_snapshot_isolation on");

      // function to add intervals to dates
      c.createStatement().executeUpdate(
          "create or alter function _core.add_interval(@Date DateTime2, @Interval nvarchar(1000)) returns DateTime2 as\n" +
              "begin\n" +
              "  declare @IncDate DateTime2;\n" +
              "  declare @IntervalPart nvarchar(20);\n" +
              "  declare @IntervalParts Cursor;\n" +
              "  declare @Indicator nvarchar(2);\n" +

              "  set @IncDate = @Date;\n" +
              "  set @IntervalParts = cursor fast_forward for select * from string_split(@Interval, ',');\n" +

              "  open @IntervalParts;\n" +
              "  fetch next from @IntervalParts into @IntervalPart\n" +
              "  while @@fetch_status = 0\n" +
              "  begin\n" +
              "    set @IntervalPart = upper(trim(@IntervalPart));\n" +
              "    set @Indicator = right(@IntervalPart, 1);\n" +
              "    if @Indicator = 'Y'\n" +
              "      set @IncDate = dateadd(year, cast(left(@IntervalPart, len(@IntervalPart) - 1) as int), @IncDate)\n" +

              "    else if @Indicator = 'N'\n" +
              "      set @IncDate = dateadd(month, cast(left(@IntervalPart, len(@IntervalPart) - 3) as int), @IncDate)\n" +

              "    else if @Indicator = 'W'\n" +
              "      set @IncDate = dateadd(week, cast(left(@IntervalPart, len(@IntervalPart) - 1) as int), @IncDate)\n" +

              "    else if @Indicator = 'D'\n" +
              "      set @IncDate = dateadd(day, cast(left(@IntervalPart, len(@IntervalPart) - 1) as int), @IncDate)\n" +

              "    else if @Indicator = 'H'\n" +
              "      set @IncDate = dateadd(hour, cast(left(@IntervalPart, len(@IntervalPart) - 1) as int), @IncDate)\n" +

              "    else if @Indicator = 'M'\n" +
              "      set @IncDate = dateadd(minute, cast(left(@IntervalPart, len(@IntervalPart) - 1) as int), @IncDate)\n" +

              "    else if @Indicator = 'S'\n" +
              "    begin\n" +
              "      if right(@IntervalPart, 2) = 'MS'\n" +
              "        set @IncDate = dateadd(millisecond, cast(left(@IntervalPart, len(@IntervalPart) - 2) as int), @IncDate)\n" +
              "      else\n" +
              "        set @IncDate = dateadd(second, cast(left(@IntervalPart, len(@IntervalPart) - 1) as int), @IncDate)\n" +
              "    end;\n" +

              "    fetch next from @IntervalParts into @IntervalPart;\n" +
              "  end; \n" +

              "  close @intervalparts;\n" +
              "  deallocate @intervalparts;\n" +
              "  return @incdate;\n" +
              "end;");

      // function to combine two intervals
      c.createStatement().executeUpdate(
          "create or alter function _core.add_intervals(@Interval1 nvarchar(1000), @Interval2 nvarchar(1000)) returns nvarchar(1000) as\n" +
              "begin\n" +
              "  declare @Years   Int = 0;\n" +
              "  declare @Months  Int = 0;\n" +
              "  declare @Days    Int = 0;\n" +
              "  declare @Weeks   Int = 0;\n" +
              "  declare @Hours   Int = 0;\n" +
              "  declare @Minutes Int = 0;\n" +
              "  declare @Seconds Int = 0;\n" +
              "  declare @Millis  Int = 0;\n" +

              "  declare @Interval nvarchar(1000);\n" +
              "  declare @IntervalPart nvarchar(20);\n" +
              "  declare @IntervalParts Cursor;\n" +
              "  declare @IntervalsCursor Cursor;\n" +
              "  declare @Indicator nvarchar(2);\n" +

              "  declare @Intervals table (value nvarchar(1000));\n" +
              "  insert into @Intervals values (@Interval1), (@Interval2);\n" +

              "  set @IntervalsCursor = cursor fast_forward for select value from @Intervals;\n" +
              "  open @IntervalsCursor;\n" +
              "  fetch next from @IntervalsCursor into @Interval;\n" +
              "  while @@fetch_status = 0\n" +
              "  begin\n" +
              "      set @IntervalParts = Cursor fast_forward for select * from string_split(@Interval, ',');\n" +
              "      open @IntervalParts;\n" +
              "      fetch next from @IntervalParts into @IntervalPart\n" +
              "      while @@fetch_status = 0\n" +
              "      begin\n" +
              "        set @IntervalPart = upper(trim(@IntervalPart));\n" +
              "        set @Indicator = right(@IntervalPart, 1);\n" +
              "        if @Indicator = 'Y'\n" +
              "          set @Years = @Years + cast(left(@IntervalPart, len(@IntervalPart) - 1) as int)\n" +

              "        else if @Indicator = 'N'\n" +
              "          set @Months = @Months + cast(left(@IntervalPart, len(@IntervalPart) - 3) as int)\n" +

              "        else if @Indicator = 'W'\n" +
              "          set @Weeks = @Weeks + cast(left(@IntervalPart, len(@IntervalPart) - 1) as int)\n" +

              "        else if @Indicator = 'D'\n" +
              "          set @Days = @Days + cast(left(@IntervalPart, len(@IntervalPart) - 1) as int)\n" +

              "        else if @Indicator = 'H'\n" +
              "          set @Hours = @Hours + cast(left(@IntervalPart, len(@IntervalPart) - 1) as int)\n" +

              "        else if @Indicator = 'M'\n" +
              "          set @Minutes = @Minutes + cast(left(@IntervalPart, len(@IntervalPart) - 1) as int)\n" +

              "        else if @Indicator = 'S'\n" +
              "        begin\n" +
              "          if right(@IntervalPart, 2) = 'MS'\n" +
              "            set @Millis = @Millis + cast(left(@IntervalPart, len(@IntervalPart) - 2) as int)\n" +
              "          else\n" +
              "            set @Seconds = @Seconds + cast(left(@IntervalPart, len(@IntervalPart) - 1) as int)\n" +
              "        end;\n" +

              "        fetch next from @IntervalParts into @IntervalPart;\n" +
              "      end;\n" +

              "      close @intervalparts;\n" +
              "      deallocate @intervalparts;\n" +

              "      fetch next from @IntervalsCursor into @Interval\n" +
              "  end\n" +
              "  close @IntervalsCursor;\n" +
              "  deallocate @IntervalsCursor;\n" +

              "  set @Interval = '';\n" +
              "  if @Years != 0\n" +
              "    set @InterVal = @Interval + cast(@Years as nvarchar) + 'y';\n" +

              "  if @Months != 0\n" +
              "    if len(@Interval) > 0\n" +
              "      set @Interval = @Interval + ','\n" +
              "    set @InterVal = @Interval + cast(@Months as nvarchar) + 'mon';\n" +

              "  if @Days != 0\n" +
              "    if len(@Interval) > 0\n" +
              "      set @Interval = @Interval + ','\n" +
              "    set @InterVal = @Interval + cast(@Days as nvarchar) + 'd';\n" +

              "  if @Weeks != 0\n" +
              "    if len(@Interval) > 0\n" +
              "      set @Interval = @Interval + ','\n" +
              "    set @InterVal = @Interval + cast(@Weeks as nvarchar) + 'w';\n" +

              "  if @Hours != 0\n" +
              "    if len(@Interval) > 0\n" +
              "      set @Interval = @Interval + ','\n" +
              "    set @InterVal = @Interval + cast(@Hours as nvarchar) + 'h';\n" +

              "  if @Minutes != 0\n" +
              "    if len(@Interval) > 0\n" +
              "      set @Interval = @Interval + ','\n" +
              "    set @InterVal = @Interval + cast(@Minutes as nvarchar) + 'm';\n" +

              "  if @Seconds != 0\n" +
              "    if len(@Interval) > 0\n" +
              "      set @Interval = @Interval + ','\n" +
              "    set @InterVal = @Interval + cast(@Seconds as nvarchar) + 's';\n" +

              "  if @Millis != 0\n" +
              "    if len(@Interval) > 0\n" +
              "      set @Interval = @Interval + ','\n" +
              "    set @InterVal = @Interval + cast(@Millis as nvarchar) + 'ms';\n" +

              "  return @Interval;\n" +
              "end;\n");

      // function to find value from lookups
      c.createStatement().executeUpdate(
          "create or alter function _core.lookup_label0(@Code nvarchar(max),\n" +
              "                                             @Lookup nvarchar(max),\n" +
              "                                             @ShowCode bit,\n" +
              "                                             @ShowLabel bit) returns nvarchar(max) as\n" +
              "begin\n" +
              "  declare @Result nvarchar(max);\n" +
              "  select @Result=(iif(coalesce(@ShowCode, 0)=coalesce(@ShowLabel, 0),\n" +
              "                      code + ' - '+ label,\n" +
              "                      iif(coalesce(@ShowCode, 0)=1,\n" +
              "                          code,\n" +
              "                          label)))\n" +
              "    from \"_platform.lookup\".\"LookupValue\" v\n" +
              "    join \"_platform.lookup\".\"Lookup\" l on v.lookup_id=l._id\n" +
              "  where l.name=@Lookup and v.code=@Code;\n" +
              "  return @Result;\n" +
              "end;");

      c.createStatement().executeUpdate("create or alter function _core.lookup_label1(@Code nvarchar(max),\n" +
                                            "                                             @Lookup nvarchar(max),\n" +
                                            "                                             @ShowCode bit,\n" +
                                            "                                             @ShowLabel bit,\n" +
                                            "                                             @Link1 nvarchar(max)) returns nvarchar(max) as\n" +
                                            "begin\n" +
                                            "  declare @LinkCursor Cursor;\n" +
                                            "  declare @Result nvarchar(max);\n" +
                                            "\n" +
                                            "  select @Result=(iif(coalesce(@ShowCode, 0)=coalesce(@ShowLabel, 0),\n" +
                                            "                      v1.code + ' - '+ v1.label,\n" +
                                            "                      iif(coalesce(@ShowCode, 0)=1,\n" +
                                            "                          v1.code,\n" +
                                            "                          v1.label)))\n" +
                                            "\n" +
                                            "    from \"_platform.lookup\".\"LookupValue\" v0\n" +
                                            "    join \"_platform.lookup\".\"Lookup\" lookup on (v0.lookup_id=lookup._id and lookup.name=@Lookup)\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk0 on (v0._id=lk0.source_lookup_value_id and lk0.lookup_link=@Link1)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v1 on v1._id=lk0.target_lookup_value_id\n" +
                                            "\n" +
                                            "   where v0.code=@Code;\n" +
                                            "  return @Result;\n" +
                                            "end;");

      c.createStatement().executeUpdate("create or alter function _core.lookup_label2(@Code nvarchar(max),\n" +
                                            "                                             @Lookup nvarchar(max),\n" +
                                            "                                             @ShowCode bit,\n" +
                                            "                                             @ShowLabel bit,\n" +
                                            "                                             @Link1 nvarchar(max),\n" +
                                            "                                             @Link2 nvarchar(max)) returns nvarchar(max) as\n" +
                                            "begin\n" +
                                            "  declare @LinkCursor Cursor;\n" +
                                            "  declare @Result nvarchar(max);\n" +
                                            "\n" +
                                            "  select @Result=(iif(coalesce(@ShowCode, 0)=coalesce(@ShowLabel, 0),\n" +
                                            "                      v2.code + ' - '+ v2.label,\n" +
                                            "                      iif(coalesce(@ShowCode, 0)=1,\n" +
                                            "                          v2.code,\n" +
                                            "                          v2.label)))\n" +
                                            "\n" +
                                            "    from \"_platform.lookup\".\"LookupValue\" v0\n" +
                                            "    join \"_platform.lookup\".\"Lookup\" lookup on (v0.lookup_id=lookup._id and lookup.name=@Lookup)\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk0 on (v0._id=lk0.source_lookup_value_id and lk0.lookup_link=@Link1)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v1 on v1._id=lk0.target_lookup_value_id\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk1 on (v1._id=lk1.source_lookup_value_id and lk1.lookup_link=@Link2)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v2 on v2._id=lk1.target_lookup_value_id\n" +
                                            "\n" +
                                            "   where v0.code=@Code;\n" +
                                            "  return @Result;\n" +
                                            "end;");

      c.createStatement().executeUpdate("create or alter function _core.lookup_label3(@Code nvarchar(max),\n" +
                                            "                                             @Lookup nvarchar(max),\n" +
                                            "                                             @ShowCode bit,\n" +
                                            "                                             @ShowLabel bit,\n" +
                                            "                                             @Link1 nvarchar(max),\n" +
                                            "                                             @Link2 nvarchar(max),\n" +
                                            "                                             @Link3 nvarchar(max)) returns nvarchar(max) as\n" +
                                            "begin\n" +
                                            "  declare @LinkCursor Cursor;\n" +
                                            "  declare @Result nvarchar(max);\n" +
                                            "\n" +
                                            "  select @Result=(iif(coalesce(@ShowCode, 0)=coalesce(@ShowLabel, 0),\n" +
                                            "                      v3.code + ' - '+ v3.label,\n" +
                                            "                      iif(coalesce(@ShowCode, 0)=1,\n" +
                                            "                          v3.code,\n" +
                                            "                          v3.label)))\n" +
                                            "\n" +
                                            "    from \"_platform.lookup\".\"LookupValue\" v0\n" +
                                            "    join \"_platform.lookup\".\"Lookup\" lookup on (v0.lookup_id=lookup._id and lookup.name=@Lookup)\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk0 on (v0._id=lk0.source_lookup_value_id and lk0.lookup_link=@Link1)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v1 on v1._id=lk0.target_lookup_value_id\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk1 on (v1._id=lk1.source_lookup_value_id and lk1.lookup_link=@Link2)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v2 on v2._id=lk1.target_lookup_value_id\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk2 on (v2._id=lk2.source_lookup_value_id and lk2.lookup_link=@Link3)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v3 on v3._id=lk2.target_lookup_value_id\n" +
                                            "\n" +
                                            "   where v0.code=@Code;\n" +
                                            "  return @Result;\n" +
                                            "end;");

      c.createStatement().executeUpdate("create or alter function _core.lookup_label4(@Code nvarchar(max),\n" +
                                            "                                             @Lookup nvarchar(max),\n" +
                                            "                                             @ShowCode bit,\n" +
                                            "                                             @ShowLabel bit,\n" +
                                            "                                             @Link1 nvarchar(max),\n" +
                                            "                                             @Link2 nvarchar(max),\n" +
                                            "                                             @Link3 nvarchar(max),\n" +
                                            "                                             @Link4 nvarchar(max)) returns nvarchar(max) as\n" +
                                            "begin\n" +
                                            "  declare @LinkCursor Cursor;\n" +
                                            "  declare @Result nvarchar(max);\n" +
                                            "\n" +
                                            "  select @Result=(iif(coalesce(@ShowCode, 0)=coalesce(@ShowLabel, 0),\n" +
                                            "                      v4.code + ' - '+ v4.label,\n" +
                                            "                      iif(coalesce(@ShowCode, 0)=1,\n" +
                                            "                          v4.code,\n" +
                                            "                          v4.label)))\n" +
                                            "\n" +
                                            "    from \"_platform.lookup\".\"LookupValue\" v0\n" +
                                            "    join \"_platform.lookup\".\"Lookup\" lookup on (v0.lookup_id=lookup._id and lookup.name=@Lookup)\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk0 on (v0._id=lk0.source_lookup_value_id and lk0.lookup_link=@Link1)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v1 on v1._id=lk0.target_lookup_value_id\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk1 on (v1._id=lk1.source_lookup_value_id and lk1.lookup_link=@Link2)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v2 on v2._id=lk1.target_lookup_value_id\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk2 on (v2._id=lk2.source_lookup_value_id and lk2.lookup_link=@Link3)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v3 on v3._id=lk2.target_lookup_value_id\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk3 on (v3._id=lk3.source_lookup_value_id and lk3.lookup_link=@Link4)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v4 on v4._id=lk3.target_lookup_value_id\n" +
                                            "\n" +
                                            "   where v0.code=@Code;\n" +
                                            "  return @Result;\n" +
                                            "end;");

      c.createStatement().executeUpdate("create or alter function _core.lookup_label5(@Code nvarchar(max),\n" +
                                            "                                             @Lookup nvarchar(max),\n" +
                                            "                                             @ShowCode bit,\n" +
                                            "                                             @ShowLabel bit,\n" +
                                            "                                             @Link1 nvarchar(max),\n" +
                                            "                                             @Link2 nvarchar(max),\n" +
                                            "                                             @Link3 nvarchar(max),\n" +
                                            "                                             @Link4 nvarchar(max),\n" +
                                            "                                             @Link5 nvarchar(max)) returns nvarchar(max) as\n" +
                                            "begin\n" +
                                            "  declare @LinkCursor Cursor;\n" +
                                            "  declare @Result nvarchar(max);\n" +
                                            "\n" +
                                            "  select @Result=(iif(coalesce(@ShowCode, 0)=coalesce(@ShowLabel, 0),\n" +
                                            "                      v5.code + ' - '+ v5.label,\n" +
                                            "                      iif(coalesce(@ShowCode, 0)=1,\n" +
                                            "                          v5.code,\n" +
                                            "                          v5.label)))\n" +
                                            "\n" +
                                            "    from \"_platform.lookup\".\"LookupValue\" v0\n" +
                                            "    join \"_platform.lookup\".\"Lookup\" lookup on (v0.lookup_id=lookup._id and lookup.name=@Lookup)\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk0 on (v0._id=lk0.source_lookup_value_id and lk0.lookup_link=@Link1)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v1 on v1._id=lk0.target_lookup_value_id\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk1 on (v1._id=lk1.source_lookup_value_id and lk1.lookup_link=@Link2)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v2 on v2._id=lk1.target_lookup_value_id\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk2 on (v2._id=lk2.source_lookup_value_id and lk2.lookup_link=@Link3)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v3 on v3._id=lk2.target_lookup_value_id\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk3 on (v3._id=lk3.source_lookup_value_id and lk3.lookup_link=@Link4)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v4 on v4._id=lk3.target_lookup_value_id\n" +
                                            "\n" +
                                            "    join \"_platform.lookup\".\"LookupValueLink\" lk4 on (v4._id=lk4.source_lookup_value_id and lk4.lookup_link=@Link5)\n" +
                                            "    join \"_platform.lookup\".\"LookupValue\" v5 on v5._id=lk4.target_lookup_value_id\n" +
                                            "\n" +
                                            "   where v0.code=@Code;\n" +
                                            "  return @Result;\n" +
                                            "end;\n");

      c.createStatement().executeUpdate("create or alter function _core.rpad(@Str nvarchar(max), @Length int, @Pad nvarchar=' ') returns nvarchar(max)\n" +
                                            "begin\n" +
                                            "  declare @Remaining int;\n" +
                                            "  if len(@Str) < @Length\n" +
                                            "  begin\n" +
                                            "    set @Remaining = @Length - len(@Str);\n" +
                                            "    set @Str = @Str + left(replicate(@Pad, @Remaining), @Remaining);\n" +
                                            "  end;\n" +
                                            "  return @Str;\n" +
                                            "end;");

      c.createStatement().executeUpdate("create or alter function _core.lpad(@Str nvarchar(max), @Length int, @Pad nvarchar=' ') returns nvarchar(max)\n" +
                                            "begin\n" +
                                            "  declare @Remaining int;\n" +
                                            "  if len(@Str) < @Length\n" +
                                            "  begin\n" +
                                            "    set @Remaining = @Length - len(@Str);\n" +
                                            "    set @Str =  left(replicate(@Pad, @Remaining), @Remaining) + @Str;\n" +
                                            "  end;\n" +
                                            "  return @Str;\n" +
                                            "end;");

      c.createStatement().executeUpdate("create or alter function _core.range(@Val float, @Intervals nvarchar(max)) returns nvarchar(max) as\n" +
                                            "begin\n" +
                                            "  declare @Result nvarchar(max);\n" +
                                            "\n" +
                                            "  --  Holds  ranges from each entry in interval.\n" +
                                            "  declare @Range table(ord bigint, lb bigint, ub bigint, label nvarchar(1000));\n" +
                                            "\n" +
                                            "  -- E.g. 10,15,30 becomes 10-15, 15-30, 30-\n" +
                                            "  insert into @Range(ord, lb, ub)\n" +
                                            "    select 1 + row_number() over(order by cast(value as bigint)),\n" +
                                            "           cast(value as bigint) lb,\n" +
                                            "           lead(cast(value as bigint), 1, null) over(order by cast(value as bigint))\n" +
                                            "      from string_split(@Intervals, ',');\n" +
                                            "\n" +
                                            "  -- Add label for intermediate ranges (e.g. 10-15, 15-30)\n" +
                                            "  update @Range\n" +
                                            "     set label=_core.lpad(cast(ord as nvarchar), 2, '0') + '. ' + cast(lb as nvarchar(100)) + ' to ' + cast(ub-1 as nvarchar(100))\n" +
                                            "   where ub is not null;\n" +
                                            "\n" +
                                            "  -- Add end open range (e.g 30 or more)\n" +
                                            "  update @Range\n" +
                                            "     set label=_core.lpad(cast(ord as nvarchar), 2, '0') + '. ' + cast(lb as nvarchar(100)) + ' or more',\n" +
                                            "         ub=9999999999999999\n" +
                                            "   where ub is null;\n" +
                                            "\n" +
                                            "  -- Add start open range (< min)\n" +
                                            "  insert into @Range(lb, ub, label)\n" +
                                            "  select -9999999999999999, min(lb), '01. Less than ' + cast(min(lb) as nvarchar(max))\n" +
                                            "    from @Range\n" +
                                            "\n" +
                                            "  select @Result=label\n" +
                                            "    from @Range\n" +
                                            "   where @Val >= lb and @Val < ub;\n" +
                                            "\n" +
                                            "  return @Result;\n" +
                                            "end;");

      c.createStatement().executeUpdate(
          "create or alter function _core.floormod(@dividend Int, @divider Int) returns Int as\n" +
              "begin\n" +
              "    return ((@dividend % @divider) + @divider) % @divider;\n" +
              "end;"
      );

      c.createStatement().executeUpdate(
          "create or alter function _core.obfuscate_shift(@i Int) returns Int as\n" +
              "begin\n" +
              "  if @i % 2 = 0\n" +
              "    set @i = @i + 3\n" +
              "  else\n" +
              "    set @i = -(@i + 3);\n" +
              "  return @i\n" +
              "end;"
      );

      c.createStatement().executeUpdate(
          "create or alter function _core.unobfuscate(@obfuscated nvarchar(1000)) returns nvarchar(1000) as\n" +
              "begin\n" +
              "  declare @unobfuscated   nvarchar(1000) = '';\n" +
              "  declare @i              Int = 2;\n" +
              "  declare @pos            Int;\n" +
              "  declare @c              Char;\n" +
              "  declare @password_chars nvarchar(100) = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';\n" +
              "  declare @chars_len      Int = len(@password_chars);\n" +
              "\n" +
              "  while @i <= len(@obfuscated)\n" +
              "  begin\n" +
              "      set @c = substring(@obfuscated, @i, 1);\n" +
              "      set @pos = charindex(@c, @password_chars collate Latin1_General_CS_AS) - _core.obfuscate_shift(cast((@i - 1) / 2 as Int)) - 1;\n" +
              "      set @unobfuscated = @unobfuscated + substring(@password_chars, _core.floormod(@pos, @chars_len) + 1, 1);\n" +
              "      set @i = @i + 2;\n" +
              "  end\n" +
              "  return @unobfuscated;\n" +
              "end;\n"
      );

      c.createStatement().executeUpdate(
          "create or alter function _core.randomstr(@length Int) returns nvarchar(1000) as\n" +
              "begin\n" +
              "  declare @str            nvarchar(1000) = '';\n" +
              "  declare @i              Int = 1;\n" +
              "  declare @password_chars nvarchar(100) = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';\n" +
              "  declare @chars_len      Int = len(@password_chars);\n" +
              "  declare @random         BigInt = datepart(millisecond, getutcdate());\n" +
              "  declare @a              BigInt = 1664525;\n" +
              "  declare @c              BigInt = 1013904223;\n" +
              "  declare @m              BigInt = power(cast(2 as bigint), 32);\n" +
              "\n" +
              "  while @i <= @length\n" +
              "  begin\n" +
              "      set @random = (@a * @random + @c) % @m;\n" +
              "      set @str = @str + substring(@password_chars, @random % @chars_len + 1, 1);\n" +
              "      set @i = @i + 1;\n" +
              "  end\n" +
              "  return @str;\n" +
              "end;\n"
      );

      c.createStatement().executeUpdate(
          "create or alter function _core.obfuscate(@unobfuscated nvarchar(1000)) returns nvarchar(1000) as\n" +
              "begin\n" +
              "  declare @obfuscated     nvarchar(1000) = '';\n" +
              "  declare @i              Int = 1;\n" +
              "  declare @pos            Int;\n" +
              "  declare @ch              Char;\n" +
              "  declare @password_chars nvarchar(100) = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';\n" +
              "  declare @chars_len      Int = len(@password_chars);\n" +
              "  declare @random         BigInt = datepart(millisecond, getutcdate());\n" +
              "  declare @a              BigInt = 1664525;\n" +
              "  declare @c              BigInt = 1013904223;\n" +
              "  declare @m              BigInt = power(cast(2 as bigint), 32);\n" +
              "\n" +
              "  while @i <= len(@unobfuscated)\n" +
              "  begin\n" +
              "      set @ch = substring(@unobfuscated, @i, 1);\n" +
              "      set @pos = charindex(@ch, @password_chars collate Latin1_General_CS_AS) + _core.obfuscate_shift(@i - 1) - 1;\n" +
              "      set @random = (@a * @random + @c) % @m;\n" +
              "      set @obfuscated = @obfuscated + substring(@password_chars, @random % @chars_len + 1, 1);\n" +
              "      set @obfuscated = @obfuscated + substring(@password_chars, _core.floormod(@pos, @chars_len) + 1, 1);\n" +
              "      set @i = @i + 1;\n" +
              "  end\n" +
              "  return @obfuscated;\n" +
              "end;"
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
          "jdbc:sqlserver://"
              + valueOf(config.get("database.host"))
              + (config.containsKey("database.port")
                    ? ':' + valueOf(config.get("database.port"))
                    : "")
              + ";database=" + valueOf(config.get("database.name"))
              + ";sendStringParametersAsUnicode=true",
          username,
          password);
      con.setAutoCommit(autoCommit);
      if (isolationLevel == -1) {
//        con.setTransactionIsolation(SQLServerConnection.TRANSACTION_SNAPSHOT);
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
      if (isolationLevel == -1) {
//        con.setTransactionIsolation(SQLServerConnection.TRANSACTION_SNAPSHOT);
      } else {
        con.setTransactionIsolation(isolationLevel);
      }
      return con;
    } catch (Exception e) {
      throw unchecked(e);
    }
  }

  @Override
  public void setArray(PreparedStatement ps,
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

  private <T> T[] arrayOf(String array,
                          Class<T> componentType) {
    if (array == null) {
      return null;
    } else {
      /*
       * Arrays can be 3 different forms: a,b,c,... or [a,b,c,...] or <type>[a,b,c...].
       * For the latter two cases, extract the internal array contents before parsing.
       */
      String remapped = ARRAY_ESCAPE.map(array);
      if (remapped.charAt(array.length() - 1) == ']') {
        if (remapped.charAt(0) == '[') {
          remapped = remapped.substring(1, array.length() - 1);
        } else {
          int pos = remapped.indexOf('[');
          if (pos != -1) {
            /*
             * check if array start is prefixed with a base type;
             * remove if so.
             */
            String prefix = remapped.substring(0, pos);
            if (BASE_TYPE.matcher(prefix).matches()) {
              remapped = remapped.substring(pos + 1, remapped.length() - 1);
            }
          }
        }
      }
      String[] split = remapped.split(",");
      T[] list = (T[]) Array.newInstance(componentType, split.length);
      for (int i = 0; i < split.length; i++) {
        list[i] = (T) Convert.convert(ARRAY_ESCAPE.demap(split[i]), componentType);
      }
      return list;
    }
  }

  /**
   * Datasource for pooled connections.
   */
  private final HikariDataSource dataSource;

  private final Map<String, Object> config;
}
