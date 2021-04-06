/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.database;

import ma.vi.esql.function.*;
import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.TranslationException;
import ma.vi.esql.parser.expression.Expression;
import ma.vi.esql.parser.expression.FunctionCall;
import ma.vi.esql.parser.macro.InMonthMacroFunction;
import ma.vi.esql.parser.macro.JoinLabelMacroFunction;
import ma.vi.esql.parser.macro.LookupLabelMacroFunction;
import ma.vi.esql.type.BaseRelation;
import ma.vi.esql.type.Sequence;
import ma.vi.esql.type.Type;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.stream.Collectors.joining;
import static ma.vi.esql.function.DatePartFunction.Part.*;
import static ma.vi.esql.parser.Translatable.Target.*;
import static ma.vi.esql.type.Types.*;

/**
 * Holds type information on the objects of interest, such as tables
 * and functions, in the database.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Structure {
  public Structure(Database database) {
    this.database = database;

    /*
     * A default function used to generate a translated call for unknown functions
     */
    UnknownFunction = new Function("<unknown_function>", TopType, emptyList()) {
      @Override
      public String translate(FunctionCall call, Translatable.Target target) {
        String functionName = call.functionName();
        if (functionName.contains(".")) {
          functionName = Type.dbTableName(functionName, target);
        }
        StringBuilder st = new StringBuilder(functionName).append('(');
        if (call.distinct()) {
          st.append("distinct ");
          List<Expression<?>> distinctOn = call.distinctOn();
          if (distinctOn != null && !distinctOn.isEmpty()) {
            st.append("on (")
              .append(distinctOn.stream()
                                .map(e -> e.translate(target))
                                .collect(joining(", ")))
              .append(") ");
          }
        }
        List<Expression<?>> arguments = call.arguments();
        if (arguments != null) {
          boolean first = true;
          for (Expression<?> e: arguments) {
            if (first) {
              first = false;
            } else {
              st.append(", ");
            }
            st.append(e.translate(target));
          }
        }
        return st.append(')').toString();
      }
    };

    // existence
    /////////////////////////////////////
    function(new Function("exists", BoolType,
                          singletonList(new FunctionParameter("exists", Relation)),
                          false, null));

    // aggregates
    //////////////////////////
    function(new Function("count", LongType,
                          singletonList(new FunctionParameter("count", TopType)),
                          true, null));

    function(new Function("sum", AsParameterType,
                          singletonList(new FunctionParameter("sum", NumberType)),
                          true, null));

    function(new Function("avg", FractionalType,
                          singletonList(new FunctionParameter("avg", NumberType)),
                          true, null));

    function(new Function("var_pop", FractionalType,
                          singletonList(new FunctionParameter("var", NumberType)),
                          true,
                          Map.of(POSTGRESQL, "var_pop",
                                 HSQLDB, "var_pop",
                                 SQLSERVER, "varp")));

    function(new Function("var_samp", FractionalType,
                          singletonList(new FunctionParameter("var", NumberType)),
                          true,
                          Map.of(POSTGRESQL, "var_samp",
                                 HSQLDB, "var_samp",
                                 SQLSERVER, "var")));

    function(new Function("stddev_pop", FractionalType,
                          singletonList(new FunctionParameter("stddev", NumberType)),
                          true,
                          Map.of(POSTGRESQL, "stddev_pop",
                                 HSQLDB, "stddev_pop",
                                 SQLSERVER, "stddevp")));

    function(new Function("stddev_samp", FractionalType,
                          singletonList(new FunctionParameter("stddev", NumberType)),
                          true,
                          Map.of(POSTGRESQL, "stddev_samp",
                                 HSQLDB, "stddev_samp",
                                 SQLSERVER, "stddev")));

    function(new Function("max", AsParameterType,
                          singletonList(new FunctionParameter("max", TopType)),
                          true, null));

    function(new Function("min", AsParameterType,
                          singletonList(new FunctionParameter("min", TopType)),
                          true, null));

    function(new Function("string_agg", TextType,
                          asList(new FunctionParameter("val", TopType),
                                 new FunctionParameter("delimiter", TextType)),
                          true,
                          Map.of(POSTGRESQL, "string_agg",
                                 HSQLDB, "group_concat",
                                 SQLSERVER, "string_agg")));

    function(new Function("array_agg", TextType,
                          asList(new FunctionParameter("val", TopType),
                                 new FunctionParameter("delimiter", TextType)),
                          true,
                          Map.of(POSTGRESQL, "array_agg",
                                 HSQLDB, "array_agg",
                                 SQLSERVER, "string_agg")));

    // mathematical
    //////////////////////

    // trigonometric
    function(new Function("acos", FloatType,
                          singletonList(new FunctionParameter("p", NumberType))));

    function(new Function("asin", FloatType,
                          singletonList(new FunctionParameter("p", NumberType))));

    function(new Function("atan", FloatType,
                          singletonList(new FunctionParameter("p", NumberType))));

    function(new Function("atan2", FractionalType,
                          singletonList(new FunctionParameter("stddev", NumberType)),
                          false,
                          Map.of(POSTGRESQL, "atan2",
                                 HSQLDB, "atan2",
                                 SQLSERVER, "atn2")));

    function(new Function("cos", FloatType,
                          singletonList(new FunctionParameter("p", NumberType))));

    function(new Function("sin", FloatType,
                          singletonList(new FunctionParameter("p", NumberType))));

    function(new Function("tan", FloatType,
                          singletonList(new FunctionParameter("p", NumberType))));

    function(new Function("cot", FloatType,
                          singletonList(new FunctionParameter("p", NumberType))));

    function(new Function("degrees", FloatType,
                          singletonList(new FunctionParameter("p", NumberType))));

    function(new Function("radians", FloatType,
                          singletonList(new FunctionParameter("p", NumberType))));

    // number manipulation
    function(new Function("abs", AsParameterType,
                          singletonList(new FunctionParameter("abs", NumberType))));

    function(new Function("ceil", LongType,
                          singletonList(new FunctionParameter("p", NumberType))));

    function(new Function("floor", LongType,
                          singletonList(new FunctionParameter("p", NumberType))));

    function(new Function("round", LongType,
                          Arrays.asList(new FunctionParameter("p", NumberType),
                                        new FunctionParameter("p", IntType))));

    function(new Function("sign", IntType,
                          singletonList(new FunctionParameter("p", NumberType))));

    // logarithmic
    function(new Function("ln", FloatType,
                          singletonList(new FunctionParameter("p", NumberType)),
                          false,
                          Map.of(POSTGRESQL, "ln",
                                 HSQLDB, "ln",
                                 SQLSERVER, "log")));

    function(new Function("log", FloatType,
                          singletonList(new FunctionParameter("p", NumberType)),
                          false,
                          Map.of(POSTGRESQL, "log",
                                 HSQLDB, "log10",
                                 SQLSERVER, "log10")));

    function(new Function("floormod", NumberType,
                          Arrays.asList(new FunctionParameter("dividend", NumberType),
                                        new FunctionParameter("divider", NumberType)),
                          false,
                          Map.of(POSTGRESQL, "_core.floormod",
                                 SQLSERVER, "_core.floormod")));

    function(new Function("exp", FloatType,
                          singletonList(new FunctionParameter("p", NumberType))));

    // other misc mathematical
    function(new Function("sqrt", DoubleType,
                          singletonList(new FunctionParameter("p", NumberType))));

    function(new Function("pi", FloatType, emptyList()));

    function(new Function("random", FloatType, emptyList(),
                          false,
                          Map.of(POSTGRESQL, "random",
                                 HSQLDB, "rand",
                                 SQLSERVER, "rand")));

    function(new Function("random", StringType,
                          singletonList(new FunctionParameter("length", IntType)),
                          false,
                          Map.of(POSTGRESQL, "_core.randomstr",
                                 SQLSERVER, "_core.randomstr")));

    // id
    /////////////////////////////////
    function(new Function("newid", FloatType, emptyList(),
                          false,
                          Map.of(POSTGRESQL, "uuid_generate_v4",
                                 SQLSERVER, "newid",
                                 HSQLDB, "uuid",
                                 MARIADB, "uuid",
                                 MYSQL, "uuid",
                                 JAVASCRIPT, "uuidv4")));

    // string
    /////////////////////////////////
    function(new LengthFunction());

    function(new Function("ascii", IntType,
                          singletonList(new FunctionParameter("p", StringType))));

    function(new ConcatFunction());

    function(new Function("concatws", StringType,
                          singletonList(new FunctionParameter("p", StringType)),
                          false,
                          Map.of(POSTGRESQL, "concat_ws",
                                 SQLSERVER, "concat_ws")));

    function(new Function("chr", IntType,
                          singletonList(new FunctionParameter("p", StringType)),
                          false,
                          Map.of(POSTGRESQL, "chr",
                                 SQLSERVER, "char")));

    function(new TrimFunction());
    function(new LeftTrimFunction());
    function(new RightTrimFunction());
    function(new LeftPad());
    function(new RightPad());

    function(new Function("reverse", StringType,
                          singletonList(new FunctionParameter("p", StringType))));

    function(new Function("replace", StringType,
                          asList(new FunctionParameter("s", StringType),
                                 new FunctionParameter("search_for", StringType),
                                 new FunctionParameter("replace_with", StringType))));

    function(new Function("repeat", StringType,
                          asList(new FunctionParameter("s", StringType),
                                 new FunctionParameter("times", IntType)),
                          false,
                          Map.of(POSTGRESQL, "repeat",
                                 SQLSERVER, "replicate")));

    function(new SubstringFunction());

    function(new Function("translate", StringType,
                          asList(new FunctionParameter("s", StringType),
                                 new FunctionParameter("from", StringType),
                                 new FunctionParameter("to", StringType))));

    function(new LeftFunction());
    function(new RightFunction());
    function(new LowerFunction());
    function(new UpperFunction());
    function(new IndexOfFunction());

    // Obfuscation
    /////////////////////////////////
    function(new Function("obfuscate", StringType,
                          singletonList(new FunctionParameter("p", StringType)),
                          false,
                          Map.of(POSTGRESQL, "_core.obfuscate",
                                 SQLSERVER, "_core.obfuscate")));

    function(new Function("unobfuscate", StringType,
                          singletonList(new FunctionParameter("p", StringType)),
                          false,
                          Map.of(POSTGRESQL, "_core.unobfuscate",
                                 SQLSERVER, "_core.unobfuscate")));

    // conversion and formatting
    /////////////////////////////////
    function(new FormatDateFunction());

    // date and time
    /////////////////////////////////
    function(new Function("now", StringType, emptyList(),
                          false,
                          Map.of(POSTGRESQL, "now",
                                 SQLSERVER, "getdate",
                                 JAVASCRIPT, "_moment")));

    function(new AddIntervalToDateFunction());

    function(new AddIntervalsFunction());

    function(new Function("newdate", DateType,
                          asList(new FunctionParameter("y", IntType),
                                 new FunctionParameter("m", IntType),
                                 new FunctionParameter("d", IntType)),
                          false,
                          Map.of(POSTGRESQL, "make_date",
                                 SQLSERVER, "datefromparts",
                                 JAVASCRIPT, "_moment")));

    function(new NewDateTimeFunction());

    function(new NewTimeFunction());

    function(new DatePartFunction("year", Year));
    function(new DatePartFunction("quarter", Quarter));
    function(new DatePartFunction("semester", Semester));
    function(new DatePartFunction("month", Month));
    function(new DatePartFunction("day", Day));
    function(new DatePartFunction("dayofweek", DayOfWeek));
    function(new DatePartFunction("dayofyear", DayOfYear));
    function(new DatePartFunction("week", Week));
    function(new DatePartFunction("hour", Hour));
    function(new DatePartFunction("minute", Minute));
    function(new DatePartFunction("second", Second));
    function(new DatePartFunction("milli", Millisecond));
    function(new DatePartFunction("micro", Microsecond));

    function(new AgeInYears());

    function(new StartOfMonthFunction());
    function(new EndOfMonthFunction());
    function(new InMonthMacroFunction());

    // Functions to compute difference between dates in years, months, days, etc, (datediff in sql server)
    function(new DateDiffFunction("years", Year));
    function(new DateDiffFunction("months", Month));
    function(new DateDiffFunction("days", Day));
    function(new DateDiffFunction("weeks", Week));
    function(new DateDiffFunction("hours", Hour));
    function(new DateDiffFunction("minutes", Minute));
    function(new DateDiffFunction("seconds", Second));
    function(new DateDiffFunction("millis", Millisecond));
    function(new DateDiffFunction("micros", Microsecond));

    // @todo all window functions
    ////////////////////////////////
    function(new Function("rownumber",
                          LongType, emptyList(), false,
                          Map.of(POSTGRESQL, "row_number",
                                 SQLSERVER, "row_number")));

    // labels functions and macros
    ////////////////////////////////
    function(new LookupLabelMacroFunction());
    function(new LookupLabel());
    function(new JoinLabelMacroFunction());

    // Range binning
    ///////////////////////////////////
    function(new Range());
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
      throw new TranslationException("Unknown relation: " + name);
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
    relations.remove(relation.name());
    return relationsById.remove(relation.id());
  }

  public Map<String, Function> functions() {
    return unmodifiableMap(functions);
  }

  public synchronized Function function(String name) {
    return functions.get(name);
  }

  public synchronized void function(Function function) {
    functions.put(function.name, function);
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
    sequences.put(sequence.name, sequence);
  }

  public final Database database;

  /**
   * A special default function used to generate a translated call.
   */
  public final Function UnknownFunction;

  private final ConcurrentMap<String, Function> functions = new ConcurrentHashMap<>();

  /**
   * Set of tables by name.
   */
  private final ConcurrentMap<String, BaseRelation> relations = new ConcurrentHashMap<>();
  private final ConcurrentMap<UUID, BaseRelation> relationsById = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Sequence> sequences = new ConcurrentHashMap<>();
}