package ma.vi.esql.exec;

import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.Parser;

import java.util.Iterator;

/**
 * An executor is responsible for executing ESQL programs. The {@link DefaultExecutor}
 * is the default execution engine; Custom executors can be added with the
 * {@link ma.vi.esql.database.Database#addExecutor(Executor)} method by extensions.
 * Executors are kept in a stack with the last one added used for execution first.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface Executor {
  /**
   * Instantiate the executor with the specified connection and executor chain.
   * @param con Connection to use by the instantiated executor.
   * @param executors An iterator over the chain of iterators from which to obtain
   *                  the next executor to delegate execution to, if needed.
   * @return Executor instantiated with the supplied connection.
   */
  Executor with(EsqlConnection con, Iterator<Executor> executors);

  /**
   * @return The connection that the executor uses to execute the program.
   */
  EsqlConnection connection();

  /**
   * Prepares the query by substituting parameters, expanding macros, computing
   * types, applying filters, and so on.
   */
  Esql<?, ?> prepare(Esql<?, ?> esql, QueryParams qp);

  /**
   * Execute a prepared esql program.
   */
  <R> R execPrepared(Esql<?, ?> esql, QueryParams qp);

  /**
   * {@link #prepare(Esql, QueryParams) Prepares} and {@link #execPrepared(Esql, QueryParams) executes}
   * the ESQL program using this connection. Preparation includes parameter substitution,
   * macro expansion and filter applications.
   *
   * @param esql The esql statement to execute.
   * @param qp Parameters to the esql including named parameter values and filters.
   * @return The result produced by the esql command on execution.
   */
  default <R> R exec(Esql<?, ?> esql, QueryParams qp) {
    return execPrepared(prepare(esql, qp), qp);
  }

  default <R> R exec(Esql<?, ?> esql) {
    return exec(esql, null);
  }

  default <R> R exec(String esql, QueryParams qp) {
    return exec(new Parser(connection().database().structure()).parse(esql), qp);
  }

  default <R> R exec(String esql) {
    return exec(esql, null);
  }

  default Esql<?, ?> prepare(Esql<?, ?> esql) {
    return prepare(esql, null);
  }

  default Esql<?, ?> prepare(String esql, QueryParams qp) {
    return prepare(new Parser(connection().database().structure()).parse(esql), qp);
  }

  default Esql<?, ?> prepare(String esql) {
    return prepare(esql, null);
  }

  default <R> R execPrepared(Esql<?, ?> esql) {
    return execPrepared(esql, null);
  }
}
