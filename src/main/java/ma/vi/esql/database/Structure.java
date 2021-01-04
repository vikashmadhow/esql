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

    // aggregates
    //////////////////////////
    functions.put("count",
                  new Function("count", LongType,
                               singletonList(new FunctionParameter("count", TopType)),
                               true, null));

//    functions.put("countdistinct", new CountDistinctFunction(this));

    functions.put("sum",
                  new Function("sum", AsParameterType,
                               singletonList(new FunctionParameter("sum", NumberType)),
                               true, null));

    functions.put("avg", new Function("avg", FractionalType,
                                      singletonList(new FunctionParameter("avg", NumberType)),
                                      true, null));

    functions.put("varpop",
                  new Function("var_pop", FractionalType,
                               singletonList(new FunctionParameter("var", NumberType)),
                               true,
                               Map.of(POSTGRESQL, "var_pop",
                                      HSQLDB, "var_pop",
                                      SQLSERVER, "varp")));

    functions.put("varsamp",
                  new Function("var_samp", FractionalType,
                               singletonList(new FunctionParameter("var", NumberType)),
                               true,
                               Map.of(POSTGRESQL, "var_samp",
                                      HSQLDB, "var_samp",
                                      SQLSERVER, "var")));

    functions.put("stddevpop",
                  new Function("stddev_pop", FractionalType,
                               singletonList(new FunctionParameter("stddev", NumberType)),
                               true,
                               Map.of(POSTGRESQL, "stddev_pop",
                                      HSQLDB, "stddev_pop",
                                      SQLSERVER, "stddevp")));

    functions.put("stddevsamp",
                  new Function("stddev_samp", FractionalType,
                               singletonList(new FunctionParameter("stddev", NumberType)),
                               true,
                               Map.of(POSTGRESQL, "stddev_samp",
                                      HSQLDB, "stddev_samp",
                                      SQLSERVER, "stddev")));

    functions.put("max",
                  new Function("max", AsParameterType,
                               singletonList(new FunctionParameter("max", TopType)),
                               true, null));

    functions.put("min",
                  new Function("min", AsParameterType,
                               singletonList(new FunctionParameter("min", TopType)),
                               true, null));

    functions.put("stringagg",
                  new Function("string_agg", TextType,
                               asList(new FunctionParameter("val", TopType),
                                      new FunctionParameter("delimiter", TextType)),
                               true,
                                Map.of(POSTGRESQL, "string_agg",
                                       HSQLDB, "group_concat",
                                       SQLSERVER, "string_agg")));

    functions.put("arrayagg",
                  new Function("array_agg", TextType,
                               asList(new FunctionParameter("val", TopType),
                                      new FunctionParameter("delimiter", TextType)),
                               true,
                                Map.of(POSTGRESQL, "array_agg",
                                       HSQLDB, "array_agg",
                                       SQLSERVER, "string_agg")));

    // mathematical
    //////////////////////

    // trigonometric
    functions.put("acos", new Function("acos", FloatType,
        singletonList(new FunctionParameter("p", NumberType))));

    functions.put("asin", new Function("asin", FloatType,
        singletonList(new FunctionParameter("p", NumberType))));

    functions.put("atan", new Function("atan", FloatType,
        singletonList(new FunctionParameter("p", NumberType))));

    functions.put("atan2", new Function("atan2", FractionalType,
        singletonList(new FunctionParameter("stddev", NumberType)),
        false,
        Map.of(POSTGRESQL, "atan2",
               HSQLDB, "atan2",
               SQLSERVER, "atn2")));

    functions.put("cos", new Function("cos", FloatType,
        singletonList(new FunctionParameter("p", NumberType))));

    functions.put("sin", new Function("sin", FloatType,
        singletonList(new FunctionParameter("p", NumberType))));

    functions.put("tan", new Function("tan", FloatType,
        singletonList(new FunctionParameter("p", NumberType))));

    functions.put("cot", new Function("cot", FloatType,
        singletonList(new FunctionParameter("p", NumberType))));

    functions.put("degrees", new Function("degrees", FloatType,
        singletonList(new FunctionParameter("p", NumberType))));

    functions.put("radians", new Function("radians", FloatType,
        singletonList(new FunctionParameter("p", NumberType))));

    // number manipulation
    functions.put("abs", new Function("abs", AsParameterType,
        singletonList(new FunctionParameter("abs", NumberType))));

    functions.put("ceil", new Function("ceil", LongType,
        singletonList(new FunctionParameter("p", NumberType))));

    functions.put("floor", new Function("floor", LongType,
        singletonList(new FunctionParameter("p", NumberType))));

    functions.put("round", new Function("round", LongType,
        Arrays.asList(new FunctionParameter("p", NumberType),
            new FunctionParameter("p", IntType))));

    functions.put("sign", new Function("sign", IntType,
        singletonList(new FunctionParameter("p", NumberType))));

    // logarithmic
    functions.put("ln", new Function("ln", FloatType,
        singletonList(new FunctionParameter("p", NumberType)),
        false,
        Map.of(POSTGRESQL, "ln",
               HSQLDB, "ln",
               SQLSERVER, "log")));

    functions.put("log", new Function("log", FloatType,
        singletonList(new FunctionParameter("p", NumberType)),
        false,
        Map.of(POSTGRESQL, "log",
               HSQLDB, "log10",
               SQLSERVER, "log10")));

    functions.put("floormod", new Function("floormod", NumberType,
        Arrays.asList(new FunctionParameter("dividend", NumberType),
            new FunctionParameter("divider", NumberType)),
        false,
        Map.of(POSTGRESQL, "_core.floormod",
               SQLSERVER, "_core.floormod")));

    functions.put("exp", new Function("exp", FloatType,
                  singletonList(new FunctionParameter("p", NumberType))));

    // other misc mathematical
    functions.put("sqrt", new Function("sqrt", DoubleType,
        singletonList(new FunctionParameter("p", NumberType))));

    functions.put("pi", new Function("pi", FloatType, emptyList()));

    functions.put("random", new Function("random", FloatType, emptyList(),
        false,
        Map.of(POSTGRESQL, "random",
               HSQLDB,     "rand",
               SQLSERVER,  "rand")));

    functions.put("randomstr", new Function("random", StringType,
        singletonList(new FunctionParameter("length", IntType)),
        false,
        Map.of(POSTGRESQL, "_core.randomstr",
               SQLSERVER,  "_core.randomstr")));

    // id
    /////////////////////////////////
    functions.put("newid", new Function("newid", FloatType, emptyList(),
        false,
        Map.of(POSTGRESQL, "uuid_generate_v4",
               SQLSERVER,  "newid",
               HSQLDB,     "uuid",
               JAVASCRIPT, "uuidv4")));

    // string
    /////////////////////////////////
    functions.put("length", new LengthFunction(this));

    functions.put("ascii", new Function("ascii", IntType,
        singletonList(new FunctionParameter("p", StringType))));

    functions.put("concat", new ConcatFunction(this));

    functions.put("concatws", new Function("concatws", StringType,
        singletonList(new FunctionParameter("p", StringType)),
        false,
        Map.of(POSTGRESQL, "concat_ws",
               SQLSERVER,  "concat_ws")));

    functions.put("chr", new Function("chr", IntType,
        singletonList(new FunctionParameter("p", StringType)),
        false,
        Map.of(POSTGRESQL, "chr",
               SQLSERVER, "char")));

    functions.put("trim",  new TrimFunction(this));
    functions.put("ltrim", new RightTrimFunction(this));
    functions.put("rtrim", new LeftTrimFunction(this));
    functions.put("lpad",  new LeftPad(this));
    functions.put("rpad",  new RightPad(this));

    functions.put("reverse", new Function("reverse", StringType,
        singletonList(new FunctionParameter("p", StringType))));

    functions.put("replace", new Function("replace", StringType,
        asList(new FunctionParameter("s", StringType),
            new FunctionParameter("search_for", StringType),
            new FunctionParameter("replace_with", StringType))));

    functions.put("repeat", new Function("repeat", StringType,
        asList(new FunctionParameter("s", StringType),
            new FunctionParameter("times", IntType)),
        false,
        Map.of(POSTGRESQL, "repeat",
               SQLSERVER, "replicate")));

    functions.put("substring", new SubstringFunction(this));

    functions.put("translate", new Function("translate", StringType,
        asList(new FunctionParameter("s", StringType),
            new FunctionParameter("from", StringType),
            new FunctionParameter("to", StringType))));

    functions.put("leftstr",  new LeftFunction(this));
    functions.put("rightstr", new RightFunction(this));
    functions.put("lower",    new LowerFunction(this));
    functions.put("upper",    new UpperFunction(this));
    functions.put("indexof",  new IndexOfFunction(this));

    // Obfuscation
    /////////////////////////////////
    functions.put("obfuscate", new Function("obfuscate", StringType,
        singletonList(new FunctionParameter("p", StringType)),
        false,
        Map.of(POSTGRESQL, "_core.obfuscate",
               SQLSERVER, "_core.obfuscate")));

    functions.put("unobfuscate", new Function("unobfuscate", StringType,
        singletonList(new FunctionParameter("p", StringType)),
        false,
        Map.of(POSTGRESQL, "_core.unobfuscate",
               SQLSERVER, "_core.unobfuscate")));

    // conversion and formatting
    /////////////////////////////////
    functions.put("format", new FormatDateFunction(this));

    // date and time
    /////////////////////////////////
    functions.put("now", new Function("now", StringType, emptyList(),
        false,
        Map.of(POSTGRESQL, "now",
               SQLSERVER, "getdate",
               JAVASCRIPT, "_moment")));

    functions.put("incdate", new AddIntervalToDateFunction(this));

    functions.put("addintervals", new AddIntervalsFunction(this));

    functions.put("newdate", new Function("newdate", DateType,
        asList(new FunctionParameter("y", IntType),
            new FunctionParameter("m", IntType),
            new FunctionParameter("d", IntType)),
        false,
        Map.of(POSTGRESQL, "make_date",
               SQLSERVER, "datefromparts",
               JAVASCRIPT, "_moment")));

    functions.put("newdatetime", new NewDateTimeFunction(this));

    functions.put("newtime", new NewTimeFunction(this));

    functions.put("year",       new DatePartFunction(this, "year",      Year));
    functions.put("quarter",    new DatePartFunction(this, "quarter",   Quarter));
    functions.put("semester",   new DatePartFunction(this, "semester",  Semester));
    functions.put("month",      new DatePartFunction(this, "month",     Month));
    functions.put("day",        new DatePartFunction(this, "day",       Day));
    functions.put("dayofweek",  new DatePartFunction(this, "dayofweek", DayOfWeek));
    functions.put("dayofyear",  new DatePartFunction(this, "dayofyear", DayOfYear));
    functions.put("week",       new DatePartFunction(this, "week",      Week));
    functions.put("hour",       new DatePartFunction(this, "hour",      Hour));
    functions.put("minute",     new DatePartFunction(this, "minute",    Minute));
    functions.put("second",     new DatePartFunction(this, "second",    Second));
    functions.put("milli",      new DatePartFunction(this, "milli",     Millisecond));
    functions.put("micro",      new DatePartFunction(this, "micro",     Microsecond));

    functions.put("ageinyears", new AgeInYears(this));

    functions.put("startofmonth", new StartOfMonthFunction(this));
    functions.put("endofmonth",   new EndOfMonthFunction(this));
    functions.put("inmonth",      new InMonthMacroFunction(this));

    // Functions to compute difference between dates in years, months, days, etc, (datediff in sql server)
    functions.put("years",    new DateDiffFunction(this, "years",   Year));
    functions.put("months",   new DateDiffFunction(this, "months",  Month));
    functions.put("days",     new DateDiffFunction(this, "days",    Day));
    functions.put("weeks",    new DateDiffFunction(this, "weeks",   Week));
    functions.put("hours",    new DateDiffFunction(this, "hours",   Hour));
    functions.put("minutes",  new DateDiffFunction(this, "minutes", Minute));
    functions.put("seconds",  new DateDiffFunction(this, "seconds", Second));
    functions.put("millis",   new DateDiffFunction(this, "millis",  Millisecond));
    functions.put("micros",   new DateDiffFunction(this, "micros",  Microsecond));

    // @todo all window functions
    ////////////////////////////////
    functions.put("rownumber",
                  new Function("rownumber",
                               LongType, emptyList(), false,
                               Map.of(POSTGRESQL, "row_number",
                                      SQLSERVER,  "row_number")));

    // labels functions and macros
    ////////////////////////////////
//        functions.put("label", new LabelMacroFunction(this));
    functions.put("label",        new LookupLabelMacroFunction());
    functions.put("lookuplabel",  new LookupLabel(this));
    functions.put("joinlabel",    new JoinLabelMacroFunction(this));

    // Range binning
    ///////////////////////////////////
    functions.put("range", new Range(this));
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