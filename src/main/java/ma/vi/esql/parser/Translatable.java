/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser;

/**
 * A translatable can produce a representation of itself for a specific
 * target. It is implemented by all ESQL elements and types.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Translatable<T> {
  /**
   * Translation targets.
   */
  enum Target {
    ALL,
    ESQL,
    TEST,

    POSTGRESQL,
    SQLSERVER,
    HSQLDB,
    MYSQL,
    ORACLE,

    JAVA,
    JAVASCRIPT,
    JSON
  }

  /**
   * Translate the statement for running on the specified target.
   *
   * @param target The target system to compile to.
   * @return A statement adapted for the specified target system.
   */
  T translate(Target target);

  /**
   * The value of a translatable is generally its translation. In some
   * cases, this method can be overridden to return a computed value.
   * For example, a string translation will keep quotes around the text,
   * while its value will remove it.
   */
  default Object value(Target target) {
    return translate(target);
  }
}