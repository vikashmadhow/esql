package ma.vi.esql.exec;

import ma.vi.esql.syntax.Esql;

/**
 * Result transformers takes the result from a query and transformers it to an
 * iterable of some arbitrary type T. Transformers can be used to iteratively
 * change a base result into a sequence of complex objects, for instance. The
 * transformer to use must be specified in the {@link EsqlConnection#exec(Esql, ResultTransformer, QueryParams)}
 * method.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface ResultTransformer<T> {
  /**
   * Transform the result into an iterable sequence of objects of type T.
   */
  Iterable<T> transform(Result result);
}
