/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.base.lang.Errors;
import ma.vi.base.lang.NotFoundException;
import ma.vi.base.string.Strings;
import ma.vi.base.util.Numbers;
import ma.vi.esql.database.DataException;
import ma.vi.esql.database.Database;
import ma.vi.esql.semantic.type.ArrayType;
import ma.vi.esql.semantic.type.Interval;
import ma.vi.esql.semantic.type.Type;
import ma.vi.esql.semantic.type.Types;
import ma.vi.esql.syntax.query.AttributeIndex;
import ma.vi.esql.syntax.query.QueryTranslation;
import org.postgresql.util.PGInterval;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toMap;

/**
 * Holds the result of executing an ESQL query on the database and allows for the
 * result to be iterated upon. The rows returned by this Result consists of columns
 * together with their metadata, which is loaded from specific columns in the
 * underlying resultset and/or pre-calculated for attributes having literal values.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Result implements AutoCloseable {
  /**
   * Construct a Result object.
   *
   * @param db The database from which the data was loaded.
   * @param rs The underlying resultset containing the data for the query and
   *           which will be iterated over to produce the rows of this Result.
   * @param query The query translation object containing information on the source
   *              ESQL query, its SQL translation, columns and attributes mapping.
   */
  public Result(Database         db,
                ResultSet        rs,
                QueryTranslation query) {
    this.db = db;
    this.rs = rs;
    this.query = query;
    this.columnNameToIndex = query.columns() == null
                           ? emptyMap()
                           : query.columns().stream()
                                  .collect(toMap(m -> m.column().name(),
                                                 ColumnMapping::valueIndex,
                                                 (i, j) -> i,
                                                 LinkedHashMap::new));
  }

  /**
   * Returns the number of columns in the result.
   */
  public int columnsCount() {
    return query.columns() == null ? 0 : query.columns().size();
  }

  public List<ColumnMapping> columns() {
    return unmodifiableList(query.columns());
  }

  /**
   * Loads the next row in the result if there are any and returns true,
   * returns false if there are no more rows left. Initially the result is
   * positioned before any row and this method must be called to load the
   * first row, if there's any.
   */
  public boolean next() {
    try {
      return rs != null && rs.next();
    } catch (SQLException sqle) {
      throw Errors.unchecked(sqle);
    }
  }

  /**
   * Loads the previous row in the result if there are any and returns true,
   * returns false if there are no previous rows available.
   */
  public boolean previous() {
    try {
      return rs != null && rs.previous();
    } catch (SQLException sqle) {
      throw Errors.unchecked(sqle);
    }
  }

  /**
   * Returns the column at the specified index (starting from 1) of the current row.
   */
  public <T> ResultColumn<T> get(int column) {
    try {
      ColumnMapping mapping = query.columns().get(column - 1);
      if (mapping == null) {
        throw new NotFoundException("Invalid column index: " + column);
      }
      T value = convert(rs.getObject(mapping.valueIndex()), mapping.valueIndex(), mapping.valueType());

      Map<String, Object> metadata = new HashMap<>(mapping.attributes());
      for (int i = 0; i < mapping.attributeIndices().size(); i++) {
        AttributeIndex attr = mapping.attributeIndices().get(i);
        metadata.put(attr.name(), convert(rs.getObject(attr.index()), attr.index(), attr.type()));
      }
      return new ResultColumn<>(value, mapping.column(), metadata);

    } catch (SQLException sqle) {
      throw Errors.unchecked(sqle);
    }
  }

  /**
   * Returns the column with the specified name of the current row.
   */
  public <T> ResultColumn<T> get(String field) {
    if (!columnNameToIndex.containsKey(field)) {
      throw new RuntimeException("No such field: " + field);
    }
    return get(columnNameToIndex.get(field));
  }

  /**
   * Returns the value of the column (without its metadata) at the specified
   * index of the current row.
   */
  public <T> T value(int index) {
    return (T)get(index).value();
  }

  /**
   * Returns the value of the column (without its metadata) with the specified
   * name of the current row.
   */
  public <T> T value(String colName) {
    if (!columnNameToIndex.containsKey(colName)) {
      throw new RuntimeException("No such column: " + colName);
    }
    return value(columnNameToIndex.get(colName));
  }

  /**
   * Returns the column definition at the specified index in the result.
   */
  public ColumnMapping column(int index) {
    return query.columns().get(index - 1);
  }

  /**
   * Returns the column definition with the specified name in the result.
   */
  public ColumnMapping column(String field) {
    if (!columnNameToIndex.containsKey(field)) {
      throw new RuntimeException("No such field: " + field);
    }
    return column(columnNameToIndex.get(field));
  }

  /**
   * Returns the current row as map of column names to the result column (which
   * contains its value as well its metadata attribute values).
   */
  public Map<String, ResultColumn<?>> row() {
    Map<String, ResultColumn<?>> row = new HashMap<>();
    for (int i = 1; i <= columnsCount(); i++) {
      ResultColumn<Object> col = get(i);
//      if (col.value instanceof BigDecimal) {
//        col.value = ((BigDecimal)col.value).doubleValue();
//      }
      row.put(col.column().name(), col);
    }
    return row;
  }

  /**
   * Converts and normalize the value `v` based on the expected value type.
   */
  private <T> T convert(Object v, int fieldIndex, Type valueType) throws SQLException {
    if (v == null) {
      return null;
    } else {
      if (valueType == Types.BoolType && v instanceof Number) {
        /*
         * Numeric to boolean conversion for SQL Server.
         */
        v = ((Number)v).intValue() == 1 ? Boolean.TRUE : Boolean.FALSE;

      } else if (valueType == Types.IntervalType) {
        /*
         * Interval support.
         */
        if (v instanceof PGInterval i) {
          final int microseconds = (int)(i.getSeconds() * 1000000.0);
          final int milliseconds = (microseconds + ((microseconds < 0) ? -500 : 500)) / 1000;
          v = new Interval(i.getYears(), i.getMonths(), 0, i.getDays(),
                           i.getHours(), i.getMinutes(), 0, milliseconds);

        } else if (v instanceof String) {
          v = new Interval((String)v);
        } else {
          throw new DataException("Interval is in the wrong type: " + v.getClass());
        }
      } else if (valueType == Types.UuidType && v instanceof String) {
        /*
         * Normalization of UUID type to Java UUID. Postgresql produces UUID but
         * SQL server returns only a string.
         */
        v = UUID.fromString((String)v);

      } else if (valueType instanceof ArrayType arrayType) {
        /*
         * Array support.
         */
        v = db.getArray(rs, fieldIndex, Types.classOf(arrayType.componentType.name()));

      } else if (v instanceof String && Strings.isUUID((String)v)) {
        /*
         * UUID as a string. Normalize to prevent matching errors.
         */
        v = UUID.fromString((String)v);

      } else if (v instanceof Number n) {
        /*
         * Numeric normalization.
         */
        Class<?> targetClass = Types.classOf(valueType.name());
        if (Number.class.isAssignableFrom(targetClass)
         && !n.getClass().equals(targetClass)) {
          v = Numbers.convert(n, targetClass);
        }
      }
      return (T)v;
    }
  }

  /**
   * Returns the computed result attributes for the current row.
   */
  public Map<String, Object> resultAttributes() {
    try {
      Map<String, Object> attributes = new HashMap<>(query.resultAttributes());
      for (AttributeIndex attr: query.resultAttributeIndices()) {
        Object v = rs.getObject(attr.index());
        if (attr.type() == Types.BoolType && v instanceof Number) {
          /*
           * Conversion of numeric to boolean for SQL Server.
           */
          v = ((Number)v).intValue() == 1 ? Boolean.TRUE : Boolean.FALSE;
        }
        attributes.put(attr.name(), v);
      }
      return attributes;
    } catch (SQLException sqle) {
      throw Errors.unchecked(sqle);
    }
  }

  @Override
  public void close() {
    if (rs != null) {
      Errors.unchecked(rs::close);
    }
  }

  /**
   * A Result containing nothing, used as the return value of modifying queries
   * with no returning clause.
   */
  public static final Result Nothing = new Result(null, null,
                                                  new QueryTranslation(null, null,
                                                                       emptyList(), emptyList(), emptyMap()));

  /**
   * The database from which the data was loaded.
   */
  private final Database db;

  /**
   * The underlying resultset containing the data for the query and which will
   * be iterated over to produce the rows of this Result.
   */
  private final ResultSet rs;

  /**
   * The query that produced this result.
   */
  public final QueryTranslation query;

  /**
   * Maps column names to their position in the underlying resultset.
   */
  private final Map<String, Integer> columnNameToIndex;
}
