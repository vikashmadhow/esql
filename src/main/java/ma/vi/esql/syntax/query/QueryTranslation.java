/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.esql.exec.ColumnMapping;

import java.util.List;
import java.util.Map;

/**
 * Information on query translation produced when translating select, insert, update
 * and delete queries and holding information on how the different returned columns
 * and metadata map to the columns in the underlying resultset, as well as, the fully
 * translated translation to execute on the target database.
 *
 * @param query The ESQL query that was translated.
 * @param translation The resulting translation of the ESQL query.
 * @param columns The list of columns in the result. Each column is represented
 *                as a {@link ColumnMapping} object which carries information on
 *                the index of the column and the indices of its attributes in
 *                the underlying resultset along with pre-computed values for
 *                literal attributes.
 * @param resultAttributes Precomputed values for literal result attributes.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record QueryTranslation(QueryUpdate          query,
                               String               translation,
                               List<ColumnMapping>  columns,
                               Map<String, Object>  resultAttributes) {
  @Override
  public String toString() {
    return translation;
  }
}