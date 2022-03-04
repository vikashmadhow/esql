/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.translation;

import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.syntax.EsqlPath;
import org.pcollections.HashPMap;
import org.pcollections.IntTreePMap;
import org.pcollections.PMap;

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
    CUBRID,
    ORACLE,
    MARIADB,
    MYSQL,

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

  default T translate(Target         target,
                      EsqlConnection esqlCon,
                      EsqlPath       path,
                      Environment    env) {
    return translate(target,
                     esqlCon,
                     path,
                     HashPMap.empty(IntTreePMap.empty()),
                     env);
  }

  /**
   * Translate the statement for running on the specified target.
   *
   * @param target The target system to compile to.
   * @param esqlCon The active connection that the enclosing program, if running,
   *                is using for getting and writing data.
   * @param path The path of the object being translated from the
   *             root of program tree.
   * @param parameters A set of arbitrary parameters to pass to the translator.
   * @param env The execution environment, if applicable, where the translation
   *            can get values for variables that are accessible from this part
   *            of the program.
   * @return A statement adapted for the specified target system.
   */
  T translate(Target               target,
              EsqlConnection       esqlCon,
              EsqlPath             path,
              PMap<String, Object> parameters,
              Environment          env);

  default Object exec(Target               target,
                      EsqlConnection       esqlCon,
                      EsqlPath             path,
                      Environment          env) {
    return exec(target, esqlCon, path, HashPMap.empty(IntTreePMap.empty()), env);
  }

  /**
   * The value of a translatable is generally its translation. In some
   * cases, this method can be overridden to return a computed value.
   * For example, a string translation will keep quotes around the text,
   * while its value will remove it.
   */
  default Object exec(Target               target,
                      EsqlConnection       esqlCon,
                      EsqlPath             path,
                      PMap<String, Object> parameters,
                      Environment          env) {
    return translate(target,
                     esqlCon,
                     path,
                     parameters,
                     env);
  }
}