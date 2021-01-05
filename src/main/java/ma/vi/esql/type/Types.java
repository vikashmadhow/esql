/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.type;

import ma.vi.esql.parser.Translatable;
import ma.vi.esql.parser.query.Column;

import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Integer.MAX_VALUE;
import static ma.vi.base.lang.Errors.checkArgument;
import static ma.vi.esql.parser.Translatable.Target.*;

/**
 * Type information on various structures stored in the database
 * including the types themselves.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Types {
  public static Type typeOf(String typeName) {
    typeName = typeName.trim();
    if (typeName.endsWith("[]")) {
      return typeOf(typeName.substring(0, typeName.length() - 2)).array();
    } else {
      String typeNameLower = typeName.toLowerCase();
      if (typeNameLower.startsWith("character")) {
        typeName = "text";
      }
      if (typeNameLower.startsWith("double")) {
        typeName = "double";
      }
      if (typeSynonyms.containsKey(typeNameLower)) {
        typeName = typeSynonyms.get(typeNameLower);
      }
      checkArgument(esqlTypes.containsKey(typeName), typeName + " is not a known type");
      return esqlTypes.get(typeName);
    }
  }

  /**
   * Return the custom types mapped to the specified name or null
   * if no such type found.
   */
  public static Type customType(String name) {
    return customTypes.get(name);
  }

  /**
   * Sets the custom type to the specified name replacing
   * any type previously mapped to that name. Returns the
   * previously mapped type, if any.
   */
  public static Type customType(String name, Type type) {
    return customTypes.put(name, type);
  }

  /**
   * Returns the ESQL type to use for the Java type.
   */
  public static Type typeOf(Class<?> javaType) {
    String type = javaTypeMapping.get(javaType);
    if (type == null) {
      throw new UnsupportedOperationException(javaType + " is not mapped to any Esql type");
    }
    return typeOf(type);
  }

  public static Class<?> classOf(String type) {
    Class<?> cls = baseTypeMapping.get(type);
    if (cls == null) {
      throw new UnsupportedOperationException(type + " is not mapped to any Java class");
    }
    return cls;
  }

  public static String esqlType(String dbType, Translatable.Target db) {
    return switch(db) {
      case POSTGRESQL -> postgresqlToEsqlType(dbType);
      case SQLSERVER  -> sqlServerToEsqlType(dbType);
      case HSQLDB     -> hsqldbToEsqlType(dbType);
      default         -> throw new IllegalArgumentException("Type conversion from " + db + " to ESQL is unsupported");
    };
  }

  public static String sqlServerToEsqlType(String sqlServerType) {
    return sqlServerTypeMapping.get(normaliseType(sqlServerType));
  }

  public static String postgresqlToEsqlType(String postgresqlType) {
    return postgresqlTypeMapping.get(normaliseType(postgresqlType));
  }

  public static String hsqldbToEsqlType(String hsqldbType) {
    return hsqldbTypeMapping.get(normaliseType(hsqldbType));
  }

  private static String normaliseType(String typeName) {
    typeName = typeName.toLowerCase();
    typeName = typeName.replaceAll("\\W+", "");
    if (typeName.startsWith("character")) {
      typeName = "text";
    }
    if (typeName.startsWith("double")) {
      typeName = "double";
    }
    if (typeSynonyms.containsKey(typeName)) {
      typeName = typeSynonyms.get(typeName);
    }
    return typeName;
  }

  /*
  ESQL TYPE       // POSTGRESQL TYPE      SQL SERVER TYPE
  : 'byte'        // tinyint              tinyint
  | 'short'       // smallint             smallint
  | 'int'         // integer              int
  | 'long'        // bigint               bigint
  | 'float'       // real                 real
  | 'double'      // double precision     float
  | 'money'       // money                money
  | 'bool'        // boolean              bit
  | 'char'        // char(1)              char(1)
  | 'string'      // text                 varchar(8000)
  | 'text'        // text                 varchar(max)
  | 'bytes'       // bytea                varbinary(max)
  | 'date'        // date                 date
  | 'time'        // time                 time
  | 'datetime'    // timestamp            datetime2
  | 'interval'    // interval             varchar(200)     -- No interval type in SQL Server, simulated
  | 'uuid'        // uuid                 uniqueidentifier
  | 'json'        // jsonb                varchar(max)
   */
  private static final Map<String, String> sqlServerTypeMapping = new HashMap<>();
  private static final Map<String, String> postgresqlTypeMapping = new HashMap<>();
  private static final Map<String, String> hsqldbTypeMapping = new HashMap<>();
  private static final Map<Class<?>, String> javaTypeMapping = new HashMap<>();
  private static final Map<String, Class<?>> baseTypeMapping = new HashMap<>();
  private static final Map<String, String> typeSynonyms = new HashMap<>();
  private static final Map<String, Type> esqlTypes = new HashMap<>();

  private static final Map<String, Type> customTypes = new ConcurrentHashMap<>();

  public static final Type ByteType =
      new BaseType("byte", 1, true,
                   Map.of(ALL, "tinyint"));

  public static final Type ShortType =
      new BaseType("short", 2, true,
                   Map.of(ALL, "smallint"));

  public static final Type IntType =
      new BaseType("int", 4, true,
          Map.of(POSTGRESQL, "integer",
                 HSQLDB, "integer",
                 SQLSERVER, "int"));

  public static final Type LongType =
      new BaseType("long", 8, true,
                   Map.of(ALL, "bigint"));

  public static final Type FloatType =
      new BaseType("float", 4, false,
                   Map.of(ALL, "real"));

  public static final Type DoubleType =
      new BaseType("double", 8, false,
                   Map.of(POSTGRESQL, "double precision",
                          HSQLDB, "double",
                          SQLSERVER, "float"));

  public static final Type MoneyType =
      new BaseType("money", 8, false,
          Map.of(POSTGRESQL, "money",
                 HSQLDB, "double",
                 SQLSERVER, "money"));

  public static final Type BoolType =
      new BaseType("bool", 1, false,
                   Map.of(POSTGRESQL, "boolean",
                          HSQLDB, "boolean",
                          SQLSERVER, "bit"));

  public static final Type CharType =
      new BaseType("char", 1, false,
                   Map.of(POSTGRESQL, "char(1)",
                          HSQLDB, "char(1)",
                          SQLSERVER, "nchar(1)"));

  public static final Type StringType =
      new BaseType("string", 8000, false,
                   Map.of(POSTGRESQL, "text",
                          HSQLDB, "varchar(4000)",
                          SQLSERVER, "nvarchar(4000)"));

  public static final Type TextType =
      new BaseType("text", MAX_VALUE, false,
                   Map.of(POSTGRESQL, "text",
                          HSQLDB, "longvarchar",
                          SQLSERVER, "nvarchar(max)"));

  public static final Type BytesType =
      new BaseType("bytes", MAX_VALUE, false,
        Map.of(POSTGRESQL, "bytea",
               HSQLDB, "longvarbinary",
               SQLSERVER, "varbinary(max)"));

  public static final Type DateType =
      new BaseType("date", 4, false,
                   Map.of(ALL, "date"));

  public static final Type IntervalType =
      new BaseType("interval", 32, false,
                   Map.of(POSTGRESQL, "interval",
                          HSQLDB, "interval",
                          SQLSERVER, "nvarchar(200)"));

  public static final Type TimeType = 
      new BaseType("time", 4, false,
                   Map.of(ALL, "time"));

  public static final Type DatetimeType =
      new BaseType("datetime", 8, false,
                   Map.of(POSTGRESQL, "timestamp",
                          HSQLDB, "timestamp",
                          SQLSERVER, "datetime2"));

  public static final Type UuidType =
      new BaseType("uuid", 12, false,
                   Map.of(POSTGRESQL, "uuid",
                          HSQLDB, "uuid",
                          SQLSERVER, "uniqueidentifier"));

  public static final Type JsonType =
      new BaseType("json", MAX_VALUE, false,
                   Map.of(POSTGRESQL, "jsonb",
                          HSQLDB, "longvarchar",
                          SQLSERVER, "varchar(max)"));

  // Generic numeric types
  ///////////////////////////////////
  public static final Type NumberType =
      new BaseType("number", 1, false,
                   Map.of(POSTGRESQL, "double precision",
                          HSQLDB, "double",
                          SQLSERVER, "float"));       // Super type of all number types (integral and non-integral)

  public static final Type IntegralType =
      new BaseType("integral", 1, false,
                   Map.of(ALL, "bigint"));     // Super type of integral number types (byte, short, int and long)

  public static final Type FractionalType =
      new BaseType("fractional", 1, false,
                   Map.of(POSTGRESQL, "double precision",
                          HSQLDB, "double",
                          SQLSERVER, "float"));   // Super type of non-integral (float and double) number types

  // Generic and special types
  ///////////////////////////////////

  // The theoretical ancestor type of all Relations
  public static final Type Relation =
      new Relation("__rel__") {
        @Override
        public ma.vi.esql.type.Relation copy() {
          return null;
        }

        @Override
        public List<Column> columns() {
          return null;
        }
      };

  // The theoretical type of the null value (the bottom type)
  public static final Type NullType =
      new BaseType("null", 1, false,
                   Map.of(ALL, "null")) {
    @Override
    public boolean isAbstract() {
      return true;
    }
  };

  // Type of expressions returning nothing
  public static final Type VoidType =
      new BaseType("void", 0, false,
                   Map.of(ALL, "void")) {
    @Override
    public boolean isAbstract() {
      return true;
    }
  };

  // Abstract ancestral type of all types
  public static final Type TopType =
      new BaseType("top", 1, false,
                   Map.of(ALL, "top")) {
    @Override
    public boolean isAbstract() {
      return true;
    }
  };

  // A special type for functions whose return types are the
  // same as their first parameter. This type is used for
  // aggregate function such as max and sum, whose return type
  // varies based on what they are called upon.
  public static final Type AsParameterType =
      new BaseType("___", 0, false,
                   Map.of(ALL, "___")) {
    @Override
    public boolean isAbstract() {
      return true;
    }
  };

  static {
    esqlTypes.put("byte", ByteType);
    esqlTypes.put("short", ShortType);
    esqlTypes.put("int", IntType);
    esqlTypes.put("long", LongType);
    esqlTypes.put("float", FloatType);
    esqlTypes.put("double", DoubleType);
    esqlTypes.put("money", MoneyType);
    esqlTypes.put("bool", BoolType);
    esqlTypes.put("char", CharType);
    esqlTypes.put("string", StringType);
    esqlTypes.put("text", TextType);
    esqlTypes.put("bytes", BytesType);
    esqlTypes.put("date", DateType);
    esqlTypes.put("time", TimeType);
    esqlTypes.put("datetime", DatetimeType);
    esqlTypes.put("interval", IntervalType);
    esqlTypes.put("uuid", UuidType);
    esqlTypes.put("json", JsonType);

    // abstract numeric types
    esqlTypes.put("number", NumberType);
    esqlTypes.put("integral", IntegralType);
    esqlTypes.put("fractional", FractionalType);

    // abstract and special types for edge-cases and completing the base type system
    esqlTypes.put("top", TopType);
    esqlTypes.put("null", NullType);
    esqlTypes.put("void", VoidType);
    esqlTypes.put("___", AsParameterType);

    baseTypeMapping.put("byte", Byte.class);
    baseTypeMapping.put("short", Short.class);
    baseTypeMapping.put("int", Integer.class);
    baseTypeMapping.put("long", Long.class);
    baseTypeMapping.put("float", Float.class);
    baseTypeMapping.put("double", Double.class);
    baseTypeMapping.put("bool", Boolean.class);
    baseTypeMapping.put("char", Character.class);
    baseTypeMapping.put("text", String.class);
    baseTypeMapping.put("string", String.class);
    baseTypeMapping.put("date", Date.class);
    baseTypeMapping.put("time", Time.class);
    baseTypeMapping.put("datetime", java.util.Date.class);
    baseTypeMapping.put("interval", Interval.class);
    baseTypeMapping.put("uuid", UUID.class);

    javaTypeMapping.put(byte.class, "byte");
    javaTypeMapping.put(Byte.class, "byte");
    javaTypeMapping.put(short.class, "short");
    javaTypeMapping.put(Short.class, "short");
    javaTypeMapping.put(int.class, "int");
    javaTypeMapping.put(Integer.class, "int");
    javaTypeMapping.put(long.class, "long");
    javaTypeMapping.put(Long.class, "long");
    javaTypeMapping.put(float.class, "float");
    javaTypeMapping.put(Float.class, "float");
    javaTypeMapping.put(double.class, "double");
    javaTypeMapping.put(Double.class, "double");
    javaTypeMapping.put(boolean.class, "bool");
    javaTypeMapping.put(Boolean.class, "bool");
    javaTypeMapping.put(char.class, "char");
    javaTypeMapping.put(Character.class, "char");
    javaTypeMapping.put(String.class, "text");
    javaTypeMapping.put(Date.class, "date");
    javaTypeMapping.put(Time.class, "time");
    javaTypeMapping.put(java.util.Date.class, "datetime");
    javaTypeMapping.put(Interval.class, "interval");
    javaTypeMapping.put(UUID.class, "uuid");

    postgresqlTypeMapping.put("tinyint", "byte");
    postgresqlTypeMapping.put("smallint", "short");
    postgresqlTypeMapping.put("integer", "int");
    postgresqlTypeMapping.put("bigint", "long");
    postgresqlTypeMapping.put("real", "float");
    postgresqlTypeMapping.put("float", "float");
    postgresqlTypeMapping.put("double", "double");
    postgresqlTypeMapping.put("double precision", "double");
    postgresqlTypeMapping.put("decimal", "double");
    postgresqlTypeMapping.put("money", "money");
    postgresqlTypeMapping.put("bool", "bool");
    postgresqlTypeMapping.put("boolean", "bool");
    postgresqlTypeMapping.put("char", "string");
    postgresqlTypeMapping.put("varchar", "text");
    postgresqlTypeMapping.put("text", "text");
    postgresqlTypeMapping.put("bytea", "bytes");
    postgresqlTypeMapping.put("date", "date");
    postgresqlTypeMapping.put("time", "time");
    postgresqlTypeMapping.put("timestamp", "datetime");
    postgresqlTypeMapping.put("datetime", "datetime");
    postgresqlTypeMapping.put("interval", "interval");
    postgresqlTypeMapping.put("uuid", "uuid");
    postgresqlTypeMapping.put("json", "json");
    postgresqlTypeMapping.put("jsonb", "json");
    postgresqlTypeMapping.put("array", "text[]");
    postgresqlTypeMapping.put("anyarray", "text[]");
    postgresqlTypeMapping.put("name", "text");
    postgresqlTypeMapping.put("regproc", "long");
    postgresqlTypeMapping.put("pg_node_tree", "text");
    postgresqlTypeMapping.put("pg_ndistinct", "text");
    postgresqlTypeMapping.put("pg_dependencies", "text");
    postgresqlTypeMapping.put("pg_mcv_list", "text");
    postgresqlTypeMapping.put("xid", "long");
    postgresqlTypeMapping.put("pg_lsn", "long");

    sqlServerTypeMapping.put("tinyint", "byte");
    sqlServerTypeMapping.put("smallint", "short");
    sqlServerTypeMapping.put("int", "int");
    sqlServerTypeMapping.put("bigint", "long");
    sqlServerTypeMapping.put("real", "float");
    sqlServerTypeMapping.put("float", "double");
    sqlServerTypeMapping.put("decimal", "double");
    sqlServerTypeMapping.put("double", "double");
    sqlServerTypeMapping.put("money", "money");
    sqlServerTypeMapping.put("bit", "bool");
    sqlServerTypeMapping.put("char", "string");
    sqlServerTypeMapping.put("nchar", "string");
    sqlServerTypeMapping.put("varchar", "text");
    sqlServerTypeMapping.put("text", "text");
    sqlServerTypeMapping.put("nvarchar", "text");
    sqlServerTypeMapping.put("varbinary", "bytes");
    sqlServerTypeMapping.put("date", "date");
    sqlServerTypeMapping.put("time", "time");
    sqlServerTypeMapping.put("datetime", "datetime");
    sqlServerTypeMapping.put("datetime2", "datetime");
    sqlServerTypeMapping.put("uniqueidentifier", "uuid");

    hsqldbTypeMapping.put("tinyint", "byte");
    hsqldbTypeMapping.put("smallint", "short");
    hsqldbTypeMapping.put("integer", "int");
    hsqldbTypeMapping.put("bigint", "long");
    hsqldbTypeMapping.put("real", "float");
    hsqldbTypeMapping.put("float", "double");
    hsqldbTypeMapping.put("decimal", "double");
    hsqldbTypeMapping.put("double", "double");
    hsqldbTypeMapping.put("bool", "bool");
    hsqldbTypeMapping.put("boolean", "bool");
    hsqldbTypeMapping.put("char", "string");
    hsqldbTypeMapping.put("varchar", "text");
    hsqldbTypeMapping.put("text", "text");
    hsqldbTypeMapping.put("longvarchar", "text");
    hsqldbTypeMapping.put("longvarbinary", "bytes");
    hsqldbTypeMapping.put("date", "date");
    hsqldbTypeMapping.put("time", "time");
    hsqldbTypeMapping.put("timestamp", "datetime");
    hsqldbTypeMapping.put("interval", "interval");
    hsqldbTypeMapping.put("uuid", "uuid");
    hsqldbTypeMapping.put("array", "text[]");

    typeSynonyms.put("varchar", "text");
    typeSynonyms.put("boolean", "bool");
    typeSynonyms.put("oid", "integer");
    typeSynonyms.put("int2", "smallint");
    typeSynonyms.put("int4", "integer");
    typeSynonyms.put("int8", "bigint");
    typeSynonyms.put("real", "float");
    typeSynonyms.put("float4", "float");
    typeSynonyms.put("float8", "double");
    typeSynonyms.put("timetz", "time");
    typeSynonyms.put("timestamp with time zone", "datetime");
    typeSynonyms.put("timestampwithtimezone", "datetime");
    typeSynonyms.put("timestamp without time zone", "datetime");
    typeSynonyms.put("timestampwithouttimezone", "datetime");
    typeSynonyms.put("timestamptz", "datetime");
  }
}