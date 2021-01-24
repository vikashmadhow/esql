/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parser.expression;

import ma.vi.esql.parser.Context;
import ma.vi.esql.type.Type;
import ma.vi.esql.type.Types;

import java.util.Map;

import static ma.vi.base.string.Escape.escapeJsonString;

/**
 * A string literal in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class StringLiteral extends BaseLiteral<String> {
  public StringLiteral(Context context, String value) {
    super(context, value.startsWith("'") ? value : "'" + value + "'");
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
  public String translate(Target target, Map<String, Object> parameters) {
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

  /**
   * Escapes a string so that it can be embedded in an ESQL query.
   */
  public static String escapeEsqlString(String value) {
    return value.replace("'", "''");
  }

  @Override
  public String value(Target target) {
    /*
     * returns the string unescaped and without surrounding quotes
     */
    return value.substring(1, value.length() - 1);
  }
}