/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.parser.query;

import ma.vi.esql.exec.Mapping;
import ma.vi.esql.type.Type;
import ma.vi.base.tuple.T3;

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
public class QueryTranslation {
  public QueryTranslation(String statement,
                          List<Mapping> columns,
                          Map<String, Integer> columnToIndex,
                          List<T3<Integer, String, Type>> resultAttributeIndices,
                          Map<String, Object> resultAttributes) {
    this.statement = statement;
    this.columns = columns;
    this.columnToIndex = columnToIndex;
    this.resultAttributeIndices = resultAttributeIndices;
    this.resultAttributes = resultAttributes;
  }

  @Override
  public String toString() {
    return statement;
  }

  /**
   * Fully translated statement that can be run on the target database.
   */
  public final String statement;

  /**
   * The columns in the result of the query as a mapping which includes
   * the index of the value of the column in the resultset as well as
   * the indices of the attribute values (and values of precomputed attributes).
   */
  public final List<Mapping> columns;

  /**
   * Maps the name of columns to their index.
   */
  public final Map<String, Integer> columnToIndex;

  public final List<T3<Integer, String, Type>> resultAttributeIndices;

  public final Map<String, Object> resultAttributes;
}