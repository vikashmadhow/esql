/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import static ma.vi.base.string.Escape.escapeJsonString;

public class StringLiteral extends BaseLiteral<String> {
  public StringLiteral(Context context, String value) {
    super(context, value);
  }

  public StringLiteral(StringLiteral other) {
    super(other);
  }

  @Override
  public StringLiteral copy() {
    if (!copying()) {
      try {
        copying(true);
        return new StringLiteral(this);
      } finally {
        copying(false);
      }
    } else {
      return this;
    }
  }

  @Override
  public Type type() {
    return Types.TextType;
  }

  @Override
  public String translate(Target target) {
    /*
     * In sql server special characters can be sent as-is
     * to the database. Sql server has no support for escape
     * non-printable characters.
     */
    return switch (target) {
      case SQLSERVER  -> 'N' + value;
      case HSQLDB     -> value;
      case POSTGRESQL -> esqlToPostgresqlString(value);
      case JSON       -> '`' + escapeJsonString(value.substring(1, value.length() - 1)) + '`';
      default         -> value; // Javascript and ESQL
    };
  }

//  public static String toEsqlLiteral(Object value) {
//    if (value == null) {
//      return "null";
//
//    } else if (value instanceof Boolean) {
//      return (Boolean)value ? "true" : "false";
//
//    } else if (value instanceof String) {
//      String v = (String)value;
//      v = v.replace("%", "%%");
//      v = v.replace("'", "%q");
//      v = v.replace("\b", "%b");
//      v = v.replace("\f", "%f");
//      v = v.replace("\n", "%n");
//      v = v.replace("\r", "%r");
//      v = v.replace("\t", "%t");
//      return "'" + v + "'";
//
//    } else if (value instanceof Date) {
//      return "d'" + SERVER_DATE_TIME_FORMAT.format((Date)value) + "'";
//
//    } else {
//      return value.toString();
//    }
//  }

//  public static final Escape esqlToSqlEscape = new Escape('%', "qbfnrt%");

  public static String esqlToPostgresqlString(String value) {
    StringBuilder st = new StringBuilder();
    boolean hasFrontSlash = false;
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      hasFrontSlash |= c == '\\';
      st.append(switch (c) {
        case '\\' -> "\\\\";
        case '\b' -> "\\b";
        case '\f' -> "\\f";
        case '\n' -> "\\n";
        case '\r' -> "\\r";
        case '\t' -> "\\t";
        default   -> c;
      });
    }
    if (hasFrontSlash) {
      st.insert(0, 'E');
    }
    return st.toString();
  }

//  public static String esqlToSqlServerString(String value) {
//    return esqlToSqlEscape.map(value, new String[]{"''", "\b", "\f", "\n", "\r", "\t", "%"});
////        return value.replace("%q", "''")
////                    .replace("%b", "\b")
////                    .replace("%f", "\f")
////                    .replace("%n", "\n")
////                    .replace("%r", "\r")
////                    .replace("%t", "\t")
////                    .replace("%%", "%");
//  }

  /**
   * Escapes a string so that it can be embedded in an ESQL query.
   */
  public static String escapeEsqlString(String value) {
    return value.replace("'", "''");
//    return value.replace("%", "%%")
//                .replace("'", "%q")
//                .replace("\b", "%b")
//                .replace("\f", "%f")
//                .replace("\n", "%n")
//                .replace("\r", "%r")
//                .replace("\t", "%t");
  }

//  /**
//   * Unescape a string obtained from an ESQL context to produce its actual value.
//   */
//  public static String unescapeEsqlString(CharSequence value) {
//    return esqlToSqlEscape.map(value.toString(), new String[]{"'", "\b", "\f", "\n", "\r", "\t", "%"});
////        return value.toString()
////                    .replace("%q", "'")
////                    .replace("%b", "\b")
////                    .replace("%f", "\f")
////                    .replace("%n", "\n")
////                    .replace("%r", "\r")
////                    .replace("%t", "\t")
////                    .replace("%%", "%");
////        return insertUnicodeCodePoints(s);
//  }

  @Override
  public String value(Target target) {
    /*
     * returns the string unescaped and without surrounding quotes
     */
//    return unescapeEsqlString(value.substring(1, value.length() - 1));
    return value.substring(1, value.length() - 1);
  }

//  /**
//   * Insert characters from unicode code points. Unicodes in Esql
//   * follows the %uFFFF pattern where each F is a hexadecimal digit.
//   */
//  private static String insertUnicodeCodePoints(String s) {
//    int lastPos = 0;
//    StringBuilder st = new StringBuilder();
//    Matcher matcher = UnicodeEscape.matcher(s);
//    while (matcher.find()) {
//      st.append(s, lastPos, matcher.start());
//      st.append(toChars(parseInt(matcher.group(1), 16)));
//      lastPos = matcher.end();
//    }
//    return st.append(s.substring(lastPos)).toString();
//  }

//  /**
//   * Pattern to match unicode escapes of the form \\\\uABCD.
//   */
//  public static final Pattern UnicodeEscape = Pattern.compile("%u([0-9a-fA-F]{4})");
}