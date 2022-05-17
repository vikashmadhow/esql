/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.base.lang.Errors;
import ma.vi.base.lang.NotFoundException;
import ma.vi.base.string.Strings;
import ma.vi.base.tuple.T2;
import ma.vi.base.util.Numbers;
import ma.vi.esql.database.DataException;
import ma.vi.esql.database.Database;
import ma.vi.esql.semantic.type.*;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.query.AttributeIndex;
import ma.vi.esql.semantic.type.Column;
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
public class Result implements Iterator<Result.Row>,
                               Iterable<Result.Row>,
                               AutoCloseable {
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

  @Override
  public boolean hasNext() {
    return !endReached
        && (nextRow != null || toNext());
  }

  public boolean hasPrevious() {
    return !startReached
        && (previousRow != null || toPrevious());
  }

  @Override
  public Row next() {
    if (nextRow == null && !toNext()) {
      throw new NoSuchElementException("At end of result, no next row");
    }
    Row row = nextRow;
    nextRow = null;
    return row;
  }

  public Row previous() {
    if (previousRow == null && !toPrevious()) {
      throw new NoSuchElementException("At beginning of result, no previous row");
    }
    Row row = previousRow;
    previousRow = null;
    return row;
  }

  @Override
  public Iterator<Row> iterator() {
    return this;
  }

  /**
   * Loads the next row in the result if there are any and returns true,
   * returns false if there are no more rows left. Initially the result is
   * positioned before any row and this method must be called to load the
   * first row, if there's any.
   */
  public boolean toNext() {
    try {
      boolean next = rs != null && rs.next();
      if (next) {
        startReached = false;
        nextRow = new Row();
      } else {
        endReached = true;
      }
      return next;
    } catch (SQLException sqle) {
      throw Errors.unchecked(sqle);
    }
  }

  /**
   * Loads the previous row in the result if there are any and returns true,
   * returns false if there are no previous rows available.
   */
  public boolean toPrevious() {
    try {
      boolean previous = rs != null && rs.previous();
      if (previous) {
        endReached = false;
        previousRow = new Row();
      } else {
        startReached = true;
      }
      return previous;
    } catch (SQLException sqle) {
      throw Errors.unchecked(sqle);
    }
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

  public boolean hasColumn(String column) {
    return columnNameToIndex.containsKey(column);
  }

  /**
   * Returns the column with the specified name of the current row.
   */
  public <T> ResultColumn<T> get(String column) {
    if (!columnNameToIndex.containsKey(column)) {
      throw new RuntimeException("No such column: " + column);
    }
    return get(columnNameToIndex.get(column));
  }

  /**
   * Returns the value of the column (without its metadata) at the specified
   * index of the current row.
   */
  public <T> T value(int column) {
    return (T)get(column).value();
  }

  /**
   * Returns the value of the column (without its metadata) with the specified
   * name of the current row.
   */
  public <T> T value(String column) {
    if (!columnNameToIndex.containsKey(column)) {
      throw new RuntimeException("No such column: " + column);
    }
    return value(columnNameToIndex.get(column));
  }

  /**
   * Returns the column definition at the specified index in the result.
   */
  public ColumnMapping column(int column) {
    return query.columns().get(column - 1);
  }

  /**
   * Returns the column definition with the specified name in the result.
   */
  public ColumnMapping column(String column) {
    if (!columnNameToIndex.containsKey(column)) {
      throw new RuntimeException("No such column: " + column);
    }
    return column(columnNameToIndex.get(column));
  }

  /**
   * Returns the current row as map of column names to the result column (which
   * contains its value as well its metadata attribute values).
   */
  public Map<String, ResultColumn<?>> row() {
    Map<String, ResultColumn<?>> row = new LinkedHashMap<>();
    for (int i = 1; i <= columnsCount(); i++) {
      ResultColumn<Object> col = get(i);
      row.put(col.column().name(), col);
    }
    return row;
  }

  /**
   * Returns the current row as map of column names to value.
   */
  public Map<String, Object> valueRow() {
    Map<String, Object> row = new LinkedHashMap<>();
    for (int i = 1; i <= columnsCount(); i++) {
      ResultColumn<Object> col = get(i);
      row.put(col.column().name(), col.value());
    }
    return row;
  }

  /**
   * Returns the computed result attributes for the current row.
   */
  public Map<String, Object> resultAttributes() {
    return query.resultAttributes();
  }

  /**
   * Converts and normalize the value `v` based on the expected value type.
   */
  private <T> T convert(Object value,
                        int    index,
                        Type   type) throws SQLException {
    if (value == null) {
      return null;
    } else {
      if (type instanceof Relation r) {
        /*
         * Type of selects with single columns will be a relation but the database
         * will return an actual value type when the select is in a column list
         * of an enclosing query. In such cases, the type should be the type of
         * the single column.
         */
        List<T2<Relation, Column>> cols = r.columns();
        if (cols.size() == 1) {
          Column col = cols.get(0).b;
          type = col.computeType(new EsqlPath(col));
        }
      }
      if (type == Types.BoolType && value instanceof Number) {
        /*
         * Numeric to boolean conversion for SQL Server.
         */
        value = ((Number)value).intValue() == 1 ? Boolean.TRUE : Boolean.FALSE;

      } else if (type == Types.IntervalType) {
        /*
         * Interval support.
         */
        if (value instanceof PGInterval i) {
          final int microseconds = (int)(i.getSeconds() * 1_000_000.0);
          final int milliseconds = (microseconds + ((microseconds < 0) ? -500 : 500)) / 1000;
          value = new Interval(i.getYears(), i.getMonths(), 0, i.getDays(),
                               i.getHours(), i.getMinutes(), 0, milliseconds);

        } else if (value instanceof String) {
          value = new Interval((String)value);
        } else {
          throw new DataException("Interval is in the wrong type: " + value.getClass());
        }
      } else if (type == Types.UuidType && value instanceof String) {
        /*
         * Normalization of UUID type to Java UUID. Postgresql produces UUID but
         * SQL server returns only a string.
         */
        value = UUID.fromString((String)value);

      } else if (type instanceof ArrayType arrayType) {
        /*
         * Array support.
         */
        value = db.getArray(rs, index, Types.classOf(arrayType.componentType.name()));

      } else if (value instanceof String && Strings.isUUID((String)value)) {
        /*
         * UUID as a string. Normalize to prevent matching errors.
         */
        value = UUID.fromString((String)value);

      } else if (value instanceof Number n) {
        /*
         * Numeric normalization.
         */
        Class<?> targetClass = Types.classOf(type.name());
        if (Number.class.isAssignableFrom(targetClass)
         && !n.getClass().equals(targetClass)) {
          value = Numbers.convert(n, targetClass);
        }
      }
      return (T)value;
    }
  }

  @Override
  public void close() {
    if (rs != null) {
      Errors.unchecked(rs::close);
    }
  }

  public class Row {
    public Map<String, Object> resultAttributes()          { return Result.this.resultAttributes(); }
    public int                 columnsCount()              { return Result.this.columnsCount();     }
    public List<ColumnMapping> columns     ()              { return Result.this.columns();          }
    public boolean             hasColumn   (String column) { return Result.this.hasColumn(column);  }
    public <T> ResultColumn<T> get         (int    column) { return Result.this.get(column);        }
    public <T> ResultColumn<T> get         (String column) { return Result.this.get(column);        }
    public <T> T               value       (int    column) { return Result.this.value(column);      }
    public <T> T               value       (String column) { return Result.this.value(column);      }
    public ColumnMapping       column      (int    column) { return Result.this.column(column);     }
    public ColumnMapping       column      (String column) { return Result.this.column(column);     }
  }

  /**
   * A Result containing nothing, used as the return value of modifying queries
   * with no returning clause.
   */
  public static final Result Nothing = new Result(null, null,
                                                  new QueryTranslation(null, null,
                                                                       emptyList(), emptyMap()));

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

  private Row nextRow;
  private Row previousRow;

  private boolean endReached;
  private boolean startReached = true;
}
