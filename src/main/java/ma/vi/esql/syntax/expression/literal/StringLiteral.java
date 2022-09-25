/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.PMap;

/**
 * A string literal in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class StringLiteral extends BaseLiteral<String> {
  public StringLiteral(Context context, String value) {
    super(context, value);
  }

  public StringLiteral(StringLiteral other) {
    super(other);
  }

  @SafeVarargs
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
  public Type computeType(EsqlPath path) {
    return Types.TextType;
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    /*
     * In sql server special characters can be sent as-is
     * to the database. Sql server has no support for escape
     * non-printable characters.
     */
    return switch (target) {
      case SQLSERVER  -> parameters.containsKey("inArray")
                       ? value
                       : "N'" + value.replaceAll("'", "''") + '\'';
      case POSTGRESQL -> esqlToPostgresqlString(value);
      case JAVASCRIPT -> '`' + value.replaceAll("`", "\\`") + '`';
      default         -> "'" + value + "'"; // ESQL
    };
  }

  @Override
  public void _toString(StringBuilder st, int level, int indent) {
    st.append('\'').append(value).append('\'');
  }

  public static String esqlToPostgresqlString(String value) {
    StringBuilder st = new StringBuilder("'");
    boolean hasFrontSlash = false;
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      st.append(switch (c) {
        case '\'' ->                               "''";
        case '\\' -> { hasFrontSlash = true; yield "\\\\"; }
        case '\b' -> { hasFrontSlash = true; yield "\\b";  }
        case '\f' -> { hasFrontSlash = true; yield "\\f";  }
        case '\n' -> { hasFrontSlash = true; yield "\\n";  }
        case '\r' -> { hasFrontSlash = true; yield "\\r";  }
        case '\t' -> { hasFrontSlash = true; yield "\\t";  }
        default   -> c;
      });
    }
    if (hasFrontSlash) {
      st.insert(0, 'E');
    }
    return st.append('\'').toString();
  }

  /**
   * Escapes a string so that it can be embedded in an ESQL query.
   */
  public static String escapeEsqlString(String value) {
    return value.replace("'", "''");
  }

  @Override
  public String exec(Target               target,
                     EsqlConnection       esqlCon,
                     EsqlPath             path,
                     PMap<String, Object> parameters,
                     Environment          env) {
    return value;
  }
}