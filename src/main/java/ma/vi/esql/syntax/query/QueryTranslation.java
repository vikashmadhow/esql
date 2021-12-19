/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.query;

import ma.vi.base.tuple.T3;
import ma.vi.esql.exec.ColumnMapping;
import ma.vi.esql.semantic.type.Type;

import java.util.List;
import java.util.Map;

/**
 * Information on query translation produced when translating select, insert, update
 * and delete queries and holding information on how the different returned columns
 * and metadata map to the columns in the underlying resultset, as well as, the fully
 * translated statement to execute on the target database.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public record QueryTranslation(String                          statement,
                               List<ColumnMapping>             columns,
                               Map<String, Integer>            columnToIndex,
                               List<T3<Integer, String, Type>> resultAttributeIndices,
                               Map<String, Object>             resultAttributes) {
  @Override
  public String toString() {
    return statement;
  }
}