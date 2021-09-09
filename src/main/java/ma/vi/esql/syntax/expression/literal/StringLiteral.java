/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.Translatable;

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

  public StringLiteral(StringLiteral other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public StringLiteral copy() {
    return new StringLiteral(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public StringLiteral copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new StringLiteral(this, value, children);
  }

  @Override
  public Type type(EsqlPath path) {
    return Types.TextType;
  }

  @Override
  protected String trans(Target target, EsqlPath path, Map<String, Object> parameters) {
    /*
     * In sql server special characters can be sent as-is
     * to the database. Sql server has no support for escape
     * non-printable characters.
     */
    return switch (target) {
      case SQLSERVER  -> 'N' + value;
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
  public String value(Translatable.Target target, EsqlPath path) {
    /*
     * returns the string unescaped and without surrounding quotes
     */
    return value.substring(1, value.length() - 1);
  }
}