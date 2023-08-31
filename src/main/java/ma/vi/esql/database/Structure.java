/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import ma.vi.base.lang.NotFoundException;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.exec.function.Function;
import ma.vi.esql.exec.function.FunctionCall;
import ma.vi.esql.exec.function.FunctionParam;
import ma.vi.esql.exec.function.array.InArray;
import ma.vi.esql.exec.function.array.IntersectArray;
import ma.vi.esql.exec.function.date.*;
import ma.vi.esql.exec.function.debug.Print;
import ma.vi.esql.exec.function.number.Round;
import ma.vi.esql.exec.function.sequence.NextValue;
import ma.vi.esql.exec.function.stat.Bin;
import ma.vi.esql.exec.function.stat.BinFunction;
import ma.vi.esql.exec.function.string.*;
import ma.vi.esql.semantic.scope.AbstractScope;
import ma.vi.esql.semantic.scope.Symbol;
import ma.vi.esql.semantic.scope.SymbolAlreadyDefinedException;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Sequence;
import ma.vi.esql.semantic.type.Struct;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.translation.Translatable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static ma.vi.esql.exec.function.date.DatePart.Part.*;
import static ma.vi.esql.semantic.type.Types.*;
import static ma.vi.esql.translation.Translatable.Target.*;

/**
 * Holds type information on the objects of interest, such as tables and functions,
 * in the database. This is the top-level scope, known as the system scope, for
 * program execution.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Structure extends AbstractScope implements Environment {
  public Structure(Database database) {
    super(null);
    this.database = database;

    // existence
    /////////////////////////////////////
    function(new Function("exists", BoolType,
                          singletonList(new FunctionParam("exists", Relation)),
                          false, null));

    // aggregates
    //////////////////////////
    function(new Function("count", LongType,
                          singletonList(new FunctionParam("count", Any)),
                          true, Map.of(POSTGRESQL, "count",
                                       SQLSERVER,  "count_big")));

    function(new Function("sum", AsPromotedNumericParameterType,
                          singletonList(new FunctionParam("sum", NumberType)),
                          true, null));

    function(new Function("avg", FractionalType,
                          singletonList(new FunctionParam("avg", NumberType)),
                          true, null));

    function(new Function("varpop", FractionalType,
                          singletonList(new FunctionParam("var", NumberType)),
                          true,
                          Map.of(POSTGRESQL, "var_pop",
                                 HSQLDB,     "var_pop",
                                 SQLSERVER,  "varp")));

    function(new Function("varsamp", FractionalType,
                          singletonList(new FunctionParam("var", NumberType)),
                          true, Map.of(POSTGRESQL, "var_samp",
                                       HSQLDB,     "var_samp",
                                       SQLSERVER,  "var")));

    function(new Function("stddevpop", FractionalType,
                          singletonList(new FunctionParam("stddev", NumberType)),
                          true, Map.of(POSTGRESQL, "stddev_pop",
                                       HSQLDB,     "stddev_pop",
                                       SQLSERVER,  "stddevp")));

    function(new Function("stddevsamp", FractionalType,
                          singletonList(new FunctionParam("stddev", NumberType)),
                          true, Map.of(POSTGRESQL, "stddev_samp",
                                       HSQLDB,     "stddev_samp",
                                       SQLSERVER,  "stddev")));

    function(new Function("max", AsParameterType,
                          singletonList(new FunctionParam("max", Any)),
                          true, null));

    function(new Function("min", AsParameterType,
                          singletonList(new FunctionParam("min", Any)),
                          true, null));

    function(new Function("stringagg", TextType,
                          asList(new FunctionParam("val", Any),
                                 new FunctionParam("delimiter", TextType)),
                          true, Map.of(POSTGRESQL, "string_agg",
                                       HSQLDB,     "group_concat",
                                       SQLSERVER,  "string_agg")));

    function(new Function("arrayagg", TextType,
                          asList(new FunctionParam("val", Any),
                                 new FunctionParam("delimiter", TextType)),
                          true, Map.of(POSTGRESQL, "array_agg",
                                       HSQLDB,     "array_agg",
                                       SQLSERVER,  "string_agg")));

    // Cryptography
    /////////////////////////////

    // sha256
    // select convert(varchar(max), hashbytes('SHA2_256', 'test1'), 2)
//    functions.put("sha256", new Sha256());

    // conditional
    //////////////////////
    functions.put("coalesce", new Coalesce());


    // Sequence operations
    //////////////////////////////////
    functions.put("nextvalue", new NextValue());

    // Array operations
    //////////////////////////////////
    functions.put("inarray",   new InArray());
    functions.put("intersect", new IntersectArray());

    // mathematical
    //////////////////////

    // trigonometric
    function(new Function("acos", FloatType,
                          singletonList(new FunctionParam("p", NumberType))));

    function(new Function("asin", FloatType,
                          singletonList(new FunctionParam("p", NumberType))));

    function(new Function("atan", FloatType,
                          singletonList(new FunctionParam("p", NumberType))));

    function(new Function("atan2", FractionalType,
                          singletonList(new FunctionParam("stddev", NumberType)),
                          false,
                          Map.of(POSTGRESQL, "atan2",
                                 HSQLDB,     "atan2",
                                 SQLSERVER,  "atn2")));

    function(new Function("cos", FloatType,
                          singletonList(new FunctionParam("p", NumberType))));

    function(new Function("sin", FloatType,
                          singletonList(new FunctionParam("p", NumberType))));

    function(new Function("tan", FloatType,
                          singletonList(new FunctionParam("p", NumberType))));

    function(new Function("cot", FloatType,
                          singletonList(new FunctionParam("p", NumberType))));

    function(new Function("degrees", FloatType,
                          singletonList(new FunctionParam("p", NumberType))));

    function(new Function("radians", FloatType,
                          singletonList(new FunctionParam("p", NumberType))));

    // number manipulation
    function(new Function("abs", AsParameterType,
                          singletonList(new FunctionParam("abs", NumberType))));

    function(new Function("ceil", LongType,
                          singletonList(new FunctionParam("p", NumberType))));

    function(new Function("floor", LongType,
                          singletonList(new FunctionParam("p", NumberType))));

    function(new Round());

    function(new Function("sign", IntType,
                          singletonList(new FunctionParam("p", NumberType))));

    // logarithmic
    function(new Function("ln", FloatType,
                          singletonList(new FunctionParam("p", NumberType)),
                          false,
                          Map.of(POSTGRESQL, "ln",
                                 HSQLDB,     "ln",
                                 SQLSERVER,  "log")));

    function(new Function("log", FloatType,
                          singletonList(new FunctionParam("p", NumberType)),
                          false,
                          Map.of(POSTGRESQL, "log",
                                 HSQLDB,     "log10",
                                 SQLSERVER,  "log10")));

    function(new Function("floormod", NumberType,
                          Arrays.asList(new FunctionParam("dividend", NumberType),
                                        new FunctionParam("divider",  NumberType)),
                          false,
                          Map.of(POSTGRESQL, "_core.floormod",
                                 SQLSERVER,  "_core.floormod")));

    function(new Function("exp", FloatType,
                          singletonList(new FunctionParam("p", NumberType))));

    // other misc mathematical
    function(new Function("sqrt", DoubleType,
                          singletonList(new FunctionParam("p", NumberType))));

    function(new Function("pi", FloatType, emptyList()));

    function(new Function("random", FloatType, emptyList(),
                          false,
                          Map.of(POSTGRESQL, "random",
                                 HSQLDB,     "rand",
                                 SQLSERVER,  "rand")));

    function(new Function("randstr", StringType,
                          singletonList(new FunctionParam("length", IntType)),
                          false,
                          Map.of(POSTGRESQL, "_core.randomstr",
                                 SQLSERVER,  "_core.randomstr")));

    // id
    /////////////////////////////////
    function(new Function("newid", UuidType, emptyList(),
                          false,
                          Map.of(POSTGRESQL, "uuid_generate_v4",
                                 SQLSERVER,  "newid",
                                 HSQLDB,     "uuid",
                                 MARIADB,    "uuid",
                                 MYSQL,      "uuid",
                                 JAVASCRIPT, "uuidv4")));

    // string
    /////////////////////////////////
    function(new Length());

    function(new Function("ascii", IntType,
                          singletonList(new FunctionParam("p", StringType))));

    function(new Concat());

    function(new Function("concatws", StringType,
                          singletonList(new FunctionParam("p", StringType)),
                          false,
                          Map.of(POSTGRESQL, "concat_ws",
                                 SQLSERVER,  "concat_ws")));

    function(new Function("chr", IntType,
                          singletonList(new FunctionParam("p", StringType)),
                          false,
                          Map.of(POSTGRESQL, "chr",
                                 SQLSERVER,  "char")));

    // Print function for debugging purposes mainly
    function(new Print());

    function(new Trim());
    function(new LeftTrim());
    function(new RightTrim());
    function(new LeftPad());
    function(new RightPad());

    function(new Function("reverse", StringType,
                          singletonList(new FunctionParam("p", StringType))));

    function(new Function("replace", StringType,
                          asList(new FunctionParam("s", StringType),
                                 new FunctionParam("search_for", StringType),
                                 new FunctionParam("replace_with", StringType))));

    function(new Function("repeat", StringType,
                          asList(new FunctionParam("s", StringType),
                                 new FunctionParam("times", IntType)),
                          false,
                          Map.of(POSTGRESQL, "repeat",
                                 SQLSERVER,  "replicate")));

    function(new Substring());

    function(new Function("translate", StringType,
                          asList(new FunctionParam("s", StringType),
                                 new FunctionParam("from", StringType),
                                 new FunctionParam("to", StringType))));

    function(new Left());
    function(new Right());
    function(new Lower());
    function(new Upper());
    function(new IndexOf());

    // Check digit
    /////////////////////////////////
    function(new Function("checkdigit", LongType,
                          singletonList(new FunctionParam("p", LongType)),
                          false,
                          Map.of(POSTGRESQL, "_core.checkdigit",
                                 SQLSERVER,  "_core.checkdigit")));

    // Obfuscation
    /////////////////////////////////
    function(new Function("obfuscate", StringType,
                          singletonList(new FunctionParam("p", StringType)),
                          false,
                          Map.of(POSTGRESQL, "_core.obfuscate",
                                 SQLSERVER,  "_core.obfuscate")));

    function(new Function("unobfuscate", StringType,
                          singletonList(new FunctionParam("p", StringType)),
                          false,
                          Map.of(POSTGRESQL, "_core.unobfuscate",
                                 SQLSERVER,  "_core.unobfuscate")));

    // conversion and formatting
    /////////////////////////////////
    function(new FormatDate());

    // date and time
    /////////////////////////////////
    function(new Now());

    // Adding intervals
    function(new AddIntervalToDate());
    function(new AddIntervals());

    // create new date/time from individual components
    function(new NewDate());
    function(new NewDateTime());
    function(new NewTime());

    // Functions to get part of a date
    function(new DatePart("year",      Year));
    function(new DatePart("quarter",   Quarter));
    function(new DatePart("semester",  Semester));
    function(new DatePart("month",     Month));
    function(new DatePart("day",       Day));
    function(new DatePart("dayofweek", DayOfWeek));
    function(new DatePart("dayofyear", DayOfYear));
    function(new DatePart("week",      Week));
    function(new DatePart("hour",      Hour));
    function(new DatePart("minute",    Minute));
    function(new DatePart("second",    Second));
    function(new DatePart("milli",     Millisecond));
    function(new DatePart("micro",     Microsecond));

    // Functions to set part of date to a value
    function(new SetYear());
    function(new SetMonth());
    function(new SetDayOfMonth());
    function(new SetDayOfWeek());
    function(new SetHour());
    function(new SetMinute());
    function(new SetSecond());

    function(new AgeInYears());

    // Function to get start of the period from a date
    function(new StartOfHour());
    function(new StartOfDay());
    function(new StartOfWeek());
    function(new StartOfMonth());

    // Function to get end of the period from a date
    function(new EndOfHour());
    function(new EndOfDay());
    function(new EndOfWeek());
    function(new EndOfMonth());

    function(new InMonth());

    // Add parts to a date
    function(new AddYears());
    function(new AddMonths());
    function(new AddWeeks());
    function(new AddDays());
    function(new AddHours());
    function(new AddMinutes());
    function(new AddSeconds());
    function(new AddMilliSeconds());
    function(new AddMicroSeconds());

    // Functions to compute difference between dates in years, months, days, etc, (datediff in sql server)
    function(new DateDiff("years",   Year));
    function(new DateDiff("months",  Month));
    function(new DateDiff("days",    Day));
    function(new DateDiff("weeks",   Week));
    function(new DateDiff("hours",   Hour));
    function(new DateDiff("minutes", Minute));
    function(new DateDiff("seconds", Second));
    function(new DateDiff("millis",  Millisecond));
    function(new DateDiff("micros",  Microsecond));

    function(new MonthsCeiling());

    // @todo all window functions
    ////////////////////////////////
    function(new Function("rownum",
                          LongType, emptyList(), false,
                          Map.of(POSTGRESQL, "row_number",
                                 SQLSERVER,  "row_number")));

    // Transaction management
    //////////////////////////////
    function(new Function("curtrans",
                          LongType, emptyList(), false,
                          Map.of(POSTGRESQL, "pg_current_xact_id",
                                 SQLSERVER,  "current_transaction_id")));

    // Range binning
    ///////////////////////////////////
    function(new BinFunction());
    function(new Bin());
  }

  @Override
  public void addSymbol(Symbol symbol) throws SymbolAlreadyDefinedException {}

  @Override
  public Symbol findSymbol(String symbolName) {
    return relations.containsKey(symbolName)
         ? relations.get(symbolName)
         : functions.getOrDefault(symbolName, null);
  }

  @Override
  public boolean has(String symbol) {
    return relations.containsKey(symbol)
        || functions.containsKey(symbol);
  }

  @Override
  public <R> R get(String symbol) throws NotFoundException {
    if (relations.containsKey(symbol)) {
      return (R)relations.get(symbol);
    } else if (functions.containsKey(symbol)) {
      return (R)functions.get(symbol);
    } else {
      throw new NotFoundException("Unknown relation or function: " + symbol);
    }
  }

  @Override
  public void set(String symbol, Object value) throws NotFoundException {
    throw new UnsupportedOperationException("Values of the system environment "
                                          + "cannot be reassigned through the "
                                          + "set method of the environment.");
  }

  @Override
  public void add(String symbol, Object value) throws SymbolAlreadyDefinedException {
    if (value instanceof ma.vi.esql.semantic.type.BaseRelation r) {
      if (relations.containsKey(symbol)) {
        throw new SymbolAlreadyDefinedException("A relation named " + symbol + " already exists");
      } else {
        relation(r);
      }
    }
    else if (value instanceof Function f) {
      if (functions.containsKey(symbol)) {
        throw new SymbolAlreadyDefinedException("A function named " + symbol + " already exists");
      } else {
        function(f);
      }
    } else {
      throw new IllegalArgumentException("Only relations and functions can be "
                                       + "added to the System Environment");
    }
  }

  @Override
  public Environment parent() {
    return null;
  }

  public Map<String, BaseRelation> relations() {
    return unmodifiableMap(relations);
  }

  public Set<BaseRelation> relationsSet() {
    return new HashSet<>(relations.values());
  }

  public synchronized boolean relationExists(String name) {
    return relations.containsKey(name);
  }

  public synchronized BaseRelation relation(String name) {
    if (!relations.containsKey(name)) {
      throw new NotFoundException("Unknown relation: " + name);
    }
    return relations.get(name);
  }

  public synchronized BaseRelation relation(UUID id) {
    return relationsById.get(id);
  }

  public synchronized void relation(BaseRelation relation) {
    relations.put(relation.name(), relation);
    relationsById.put(relation.id, relation);
  }

  public synchronized BaseRelation remove(BaseRelation relation) {
//    /*
//     * Before removing this table, remove all dependent constraints from other
//     * tables that this table points to through a foreign key (if table A has a
//     * foreign key to table B, then table B has a dependent constraints on table
//     * A).
//     */
//    for (ConstraintDefinition c: relation.constraints()) {
//      if (c instanceof ForeignKeyConstraint f) {
//        BaseRelation rel = relation(f.targetTable());
//        rel.removeDependentConstraint(f);
//      }
//    }
    relations.remove(relation.name());
    return relationsById.remove(relation.id());
  }

  public Map<String, Struct> structs() {
    return unmodifiableMap(structs);
  }

  public Set<Struct> structsSet() {
    return new HashSet<>(structs.values());
  }

  public synchronized boolean structExists(String name) {
    return structs.containsKey(name);
  }

  public synchronized Struct struct(String name) {
    if (!structs.containsKey(name)) {
      throw new NotFoundException("Unknown struct: " + name);
    }
    return structs.get(name);
  }

  public synchronized Struct struct(UUID id) {
    return structsById.get(id);
  }

  public synchronized void struct(Struct struct) {
    structs.put(struct.name(), struct);
    structsById.put(struct.id, struct);
  }

  public synchronized Struct remove(Struct struct) {
    structs.remove(struct.name());
    return structsById.remove(struct.id());
  }

  public Map<String, Function> functions() {
    return unmodifiableMap(functions);
  }

  public synchronized Function function(String name) {
    return functions.get(name);
  }

  public synchronized void function(Function function) {
    functions.put(function.name(), function);
  }

  public Map<String, Sequence> sequences() {
    return unmodifiableMap(sequences);
  }

  public Set<Sequence> sequencesSet() {
    return new HashSet<>(sequences.values());
  }

  public synchronized Sequence sequence(String name) {
    return sequences.get(name);
  }

  public synchronized void sequence(Sequence sequence) {
    sequences.put(sequence.name(), sequence);
  }

  /**
   * A special function used to generate a translated call when the invoked
   * function name does not match any known functions in the structure.
   */
  public static final Function UnknownFunction = new Function("___unknown_function", Any, emptyList()) {
    @Override
    public String translatedFunctionName(FunctionCall call, Translatable.Target target, EsqlPath path) {
      String functionName = call.functionName();
      if (functionName.contains(".")) {
        functionName = Type.dbTableName(functionName, target);
      }
      return functionName;
    }
  };

  public final Database database;

  private final ConcurrentMap<String, Function> functions = new ConcurrentHashMap<>();

  /**
   * Set of tables by name.
   */
  private final ConcurrentMap<String, BaseRelation> relations = new ConcurrentHashMap<>();

  private final ConcurrentMap<UUID, BaseRelation> relationsById = new ConcurrentHashMap<>();

  private final ConcurrentMap<String, Struct> structs = new ConcurrentHashMap<>();

  private final ConcurrentMap<UUID, Struct> structsById = new ConcurrentHashMap<>();

  private final ConcurrentMap<String, Sequence> sequences = new ConcurrentHashMap<>();
}