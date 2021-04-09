/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.base.lang.Errors;
import ma.vi.base.lang.NotFoundException;
import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T3;
import ma.vi.esql.database.DataException;
import ma.vi.esql.database.Structure;
import ma.vi.esql.parser.query.Column;
import ma.vi.esql.type.*;
import org.postgresql.util.PGInterval;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

/**
 * Holds the result of executing an ESQL query on the database and
 * allows for the result to be iterated upon.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Result implements AutoCloseable {
  public Result(Structure structure,
                ResultSet rs,
                Relation type,
                List<ColumnMapping> columns,
                Map<String, Integer> columnNameToIndex,
                List<T3<Integer, String, Type>> resultAttributeIndices,
                Map<String, Object> resultAttributes) {
    this.structure = structure;
    this.rs = rs;
    this.type = type;
    this.columns = columns;
    this.columnNameToIndex = columnNameToIndex;
    this.resultAttributes = resultAttributes;
    this.resultAttributeIndices = resultAttributeIndices;
  }

  public Structure structure() {
    return structure;
  }

  /**
   * Type of result.
   */
  public Relation type() {
    return type;
  }

  /**
   * Returns the number of columns in the result.
   */
  public int columns() {
    return columns == null ? 0 : columns.size();
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
   * Loads the next row in the result if there are any and returns true,
   * returns false if there are no more rows left. Initially the result is
   * positioned before any row and this method must be called to load the
   * first row, if there's any.
   */
  public boolean previous() {
    try {
      return rs != null && rs.previous();
    } catch (SQLException sqle) {
      throw Errors.unchecked(sqle);
    }
  }

  /**
   * Returns the column at the specified field index (starting from 1)
   * of the current row.
   */
  public <T> ResultColumn<T> get(int column) {
    try {
      ColumnMapping mapping = columns.get(column - 1);
      if (mapping == null) {
        throw new NotFoundException("Invalid column index: " + column);
      }
      T value = convert(rs.getObject(mapping.valueIndex), mapping.valueIndex, mapping.valueType);

      Map<String, Object> metadata = new HashMap<>(mapping.attributes);
      for (int i = 0; i < mapping.attributeIndices.size(); i++) {
        T3<Integer, String, Type> attr = mapping.attributeIndices.get(i);
        metadata.put(attr.b, convert(rs.getObject(attr.a), attr.a, attr.c));
      }
      return new ResultColumn<>(value, mapping.column, metadata);

    } catch (SQLException sqle) {
      throw Errors.unchecked(sqle);
    }
  }

  public <T> ResultColumn<T> get(String field) {
    if (!columnNameToIndex.containsKey(field)) {
      throw new RuntimeException("No such field: " + field);
    }
    return get(columnNameToIndex.get(field));
  }

  public <T> T value(String field) {
    if (!columnNameToIndex.containsKey(field)) {
      throw new RuntimeException("No such field: " + field);
    }
    return value(columnNameToIndex.get(field));
  }

  public Column column(int column) {
    return columns.get(column - 1).column;
  }

  public Column column(String field) {
    if (!columnNameToIndex.containsKey(field)) {
      throw new RuntimeException("No such field: " + field);
    }
    return column(columnNameToIndex.get(field));
  }

  public <T> T value(int index) {
    return (T)get(index).value;
  }

  public boolean booleanValue(String field) {
    if (!columnNameToIndex.containsKey(field)) {
      throw new RuntimeException("No such field: " + field);
    }
    return booleanValue(columnNameToIndex.get(field));
  }

  public boolean booleanValue(int index) {
    Boolean value = (Boolean)get(index).value;
    return value != null && value;
  }

  public Map<String, ResultColumn<?>> getRow() {
    Map<String, ResultColumn<?>> row = new HashMap<>();
    for (int i = 1; i <= columns(); i++) {
      ResultColumn<Object> col = get(i);
      if (col.value instanceof BigDecimal) {
        col.value = ((BigDecimal)col.value).doubleValue();
      }
      row.put(col.column.alias(), col);
    }
    return row;
  }

  private <T> T convert(Object v, int fieldIndex, Type valueType) throws SQLException {
    if (v == null) {
      return null;
    } else {
      if (valueType == Types.BoolType && v instanceof Number) {
        /*
         * conversion of numeric to boolean for SQL Server
         */
        v = ((Number)v).intValue() == 1 ? Boolean.TRUE : Boolean.FALSE;

      } else if (valueType == Types.IntervalType) {
        /*
         * interval support
         */
        if (v instanceof PGInterval) {
          PGInterval i = (PGInterval)v;
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
         * normalization of UUID type to Java UUID. Postgresql produces UUID
         * but SQL server returns only a string
         */
        v = UUID.fromString((String)v);

      } else if (valueType instanceof ArrayType) {
        /*
         * array support
         */
        ArrayType arrayType = (ArrayType)valueType;
        v = structure.database.getArray(rs, fieldIndex, Types.classOf(arrayType.componentType.name()));

      } else if (v instanceof String && Strings.isUUID((String)v)) {
        /*
         * UUID as a string. Normalize to prevent matching errors.
         */
        v = UUID.fromString((String)v);
      }
      return (T)v;
    }
  }

  /**
   * Returns the computed result attributes for the current row.
   */
  public Map<String, Object> resultAttributes() {
    try {
      Map<String, Object> attributes = new HashMap<>(resultAttributes);
      for (T3<Integer, String, Type> attr: resultAttributeIndices) {
        Object v = rs.getObject(attr.a);
        if (attr.c == Types.BoolType && v instanceof Number) {
          // conversion of numeric to boolean for SQL Server
          v = ((Number)v).intValue() == 1 ? Boolean.TRUE : Boolean.FALSE;
        }
        attributes.put(attr.b, v);
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

  public static final Result Nothing = new Result(null, null, null, emptyList(),
                                                  emptyMap(), emptyList(), emptyMap());

  private final Structure structure;

  private final ResultSet rs;

  private final Relation type;

  private final List<ColumnMapping> columns;

  private final Map<String, Integer> columnNameToIndex;

  private final List<T3<Integer, String, Type>> resultAttributeIndices;

  private final Map<String, Object> resultAttributes;

  public static final String HEX_DIGIT = "[a-fA-F0-9]";

  public static final Pattern UUID_VALUE = Pattern.compile(HEX_DIGIT + "{8}-" +
      HEX_DIGIT + "{4}-" +
      HEX_DIGIT + "{4}-" +
      HEX_DIGIT + "{4}-" +
      HEX_DIGIT + "{12}");
}
