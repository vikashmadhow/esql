/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.esql.exec;

import static java.util.stream.Collectors.joining;
import static ma.vi.base.util.Numbers.isIntegral;
import static org.json.JSONObject.quote;

/**
 * Utility class to serialize query result to Json.
 * The result is serialized as such:
 *
 * <pre>
 *   {
 *    table: {
 *      columns: {
 *        names: ['c1Name', 'c2Name', ..., 'cnName'],
 *        "c1Name": {
 *        }
 *      }
 *    },
 *    rows: [
 *      [r1_c1, r1_c2, ..., r1_cn],
 *      [r2_c1, r2_c2, ..., r2_cn],
 *      ...
 *      [rm_c1, rm_c2, ..., rm_cn],
 *    ]
 *   }
 * </pre>
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class JsonResult {

//  public static JSONObject of(Result rs) {
//    Map<String, Map<String, Object>> colMetadata = new HashMap<>();
//    JSONObject result = typeInfo(rs.type(), colMetadata);
//
//    /*
//     * Add records.
//     */
//    int columnsCount = rs.columns();
//    JSONArray rows = new JSONArray();
//    JSONArray displayRows = new JSONArray();
//    JSONArray rowColumnsOrder = result.getJSONArray("rowColumnsOrder");
//    while (rs.next()) {
//      JSONArray row = new JSONArray();
//      JSONObject displayRow = new JSONObject();
//      for (int i = 1; i <= columnsCount; i++) {
//        ResultColumn<?> column = rs.get(i);
//        JSONObject c = new JSONObject();
//
//        if (column.value instanceof BigDecimal) {
//          c.put("$v", ((BigDecimal)column.value).doubleValue());
//        } else {
//          c.put("$v", column.value);
//        }
//
//        /*
//         * Remove all metadata attributes that are already at
//         * the field level and have the same value from the column
//         * metadata.
//         */
//        if (column.metadata != null) {
//          Map<String, Object> fieldMeta = colMetadata.get(column.column.alias());
//          Map<String, Object> valueMeta = new HashMap<>(column.metadata);
//          for (Map.Entry<String, Object> e: fieldMeta.entrySet()) {
//            if (valueMeta.containsKey(e.getKey())) {
//              Object v = valueMeta.get(e.getKey());
//              if (v == null ? e.getValue() == null : v.equals(e.getValue())) {
//                valueMeta.remove(e.getKey());
//              }
//            }
//          }
//          if (!valueMeta.isEmpty()) {
//            c.put("$m", new JSONObject(valueMeta));
//          }
//        }
//        row.put(c);
//          if (column.value instanceof Date) {
//            JSONObject displayCol = new JSONObject(c.toMap());
//            displayCol.put("$v", js == null
//                                 ? column.value
//                                 : js.eval("js",
//                "moment.utc('" + ISO_DATE_FORMATTER.format((Date)column.value) + "')"));
//            displayRow.put(rowColumnsOrder.getString(i - 1), displayCol);
//          } else {
//            displayRow.put(rowColumnsOrder.getString(i - 1), c);
//          }
//
//      }
//      rows.put(row);
//        displayRows.put(displayRow);
//
//    }
//    result.put("rows", rows);
//      result.put("displayRows", displayRows);
//
//    return result;
//  }
//
//  /**
//   * Returns the type and columns information for the specified type as a
//   * JSON Object in the following form:
//   */
//  public static JSONObject typeInfo(Relation type) {
//    return typeInfo(type, new HashMap<>());
//  }
//
//  public static final JSONObject EMPTY_RESULT = new JSONObject();
//
//  static {
//    EMPTY_RESULT.put("columns", new JSONObject());
//    EMPTY_RESULT.put("columnsOrder", new JSONArray());
//    EMPTY_RESULT.put("rowColumnsOrder", new JSONArray());
//  }
//
//  public static JSONObject typeInfo(Relation type,
//                                    Map<String, Map<String, Object>> colMetadata) {
//    if (type == null) {
//      return EMPTY_RESULT;
//    } else {
//      /*
//       * Add table-level (projection) information.
//       */
//      Map<String, Column> singleFieldUniqueness = new HashMap<>();
//
//      String typeName;
//      String tableName = null;
//      if (type instanceof Selection) {
//        Selection selection = (Selection)type;
//        TableExpr from = selection.from();
//        typeName = from.type().name();
//        if (from instanceof SingleTableExpr) {
//          tableName = ((SingleTableExpr)from).tableName();
//        }
//      } else {
//        typeName = type.name();
//      }
//      String colNames = type.columns()
//                            .stream()
//                            .map(Column::alias)
//                            .collect(joining(","));
//
//      String key = typeName + '/' + colNames;
//      TypeInfo resultType = typeInfo.get(key);
//      if (resultType == null) {
//        JSONObject result = new JSONObject();
//        Map<String, Map<String, Object>> fieldAttributes = new HashMap<>();
//
//        result.put("name", typeName);
//
//        Map<String, Object> typeAttributes = forAttributes(type.attributes());
//        typeAttributes.put(TYPE, typeName);
//
//        /*
//         * Add uniqueness criteria
//         */
//        Relation singleTable = tableName == null ? type : structure().relation(tableName);
//        List<List<String>> multiFieldsUniqueness = new ArrayList<>();
//        for (Constraint constraint: singleTable.constraints().values()) {
//          if (constraint.type == Constraint.Type.UNIQUE) {
//            List<Field> fields = constraint.sourceFields;
//            if (fields.size() > 1) {
//              /*
//               * Only put multi-fields uniqueness at the projection level
//               */
//              multiFieldsUniqueness.add(fields.stream()
//                                              .map(Field::name)
//                                              .collect(toList()));
//            } else {
//              Field field = fields.get(0);
//              singleFieldUniqueness.put(field.name(), field);
//            }
//          }
//        }
//        if (!multiFieldsUniqueness.isEmpty()) {
//          JSONArray a = new JSONArray();
//          for (List<String> constraint: multiFieldsUniqueness) {
//            a.put(constraint);
//          }
//          typeAttributes.put("display_unique", a);
//        }
//        result.put("$m", typeAttributes);
//
//        /*
//         * Add column information.
//         */
//        int colSequence = 0;
//        JSONObject columns = new JSONObject();
//        JSONArray rowColumnsOrder = new JSONArray();
//
//        Map<Integer, List<String>> columnsOrderMap = new HashMap<>();
//        for (Field field: type.orderedFields()) {
//          rowColumnsOrder.put(field.name());
//          JSONObject column = new JSONObject();
//
//          // field attributes with default label if not provided
//          Map<String, Object> metadata = forAttributes(field.attributes());
//          fieldAttributes.put(field.name(), metadata);
//
//          JSONObject attributes = new JSONObject(metadata);
//          column.put("$m", attributes);
//
//          attributes.put("type", field.fieldType().name());
//          Field basedOn = field.basedOn();
//          if (basedOn != null) {
//            if (basedOn.typeOfField() == DERIVED) {
//              attributes.put("derived", true);
//              attributes.put("derived_expression", forExpression(basedOn.expression().expression()));
//            }
//            if (singleFieldUniqueness.containsKey(basedOn.name())) {
//              attributes.put("display_unique", true);
//            }
//          } else if (singleFieldUniqueness.containsKey(field.name())) {
//            attributes.put("display_unique", true);
//          }
//
//          if (!attributes.has(LABEL)) {
//            String name = field.name();
//            if (!name.equals("_id") && name.endsWith("_id")) {
//              name = name.substring(0, name.length() - 3);
//            }
//            attributes.put(LABEL, capFirst(expandByCase(name).toLowerCase()));
//          }
//
//          /*
//           * derived fields are read-only
//           */
//          if (field.typeOfField() == DERIVED && !attributes.has("derived")) {
//            attributes.put("derived", true);
//            attributes.put("derived_expression", forExpression(field.expression().expression()));
//
//          }
//          if (field.typeOfField() == DERIVED && !attributes.has(READ_ONLY)) {
//            attributes.put(READ_ONLY, true);
//          }
//
//          /*
//           * columns with names starting with '_' are hidden by default
//           */
//          if (field.name().startsWith("_") && !attributes.has(SHOW)) {
//            attributes.put(SHOW, false);
//          }
//
//          /*
//           * Non-null fields are required if not hidden
//           */
//          if (field.notNull()) {
//            if (attributes.has(EDIT)) {
//              attributes.put(REQUIRED, (boolean)attributes.get(EDIT));
//            } else {
//              attributes.put(REQUIRED, (!attributes.has(SHOW) || (boolean)attributes.get(SHOW)));
//            }
//          }
//
//          /*
//           * Initial value is set to the default value if any
//           */
//          if (field.typeOfField() == PERSISTENT
//              && field.expression() != null
//              && field.expression().expression() != null
//              && !attributes.has(INITIAL_VALUE)) {
//            attributes.put(INITIAL_VALUE, forExpression(field.expression().expression()));
//          }
//
//          /*
//           * Set the order of the column using either an explicitly set sequence value or
//           * a derived one based on order of definition of the field (in the projection
//           * base relation for all-columns (*)). Fields with explicit sequence numbers are
//           * given priority to their specified positions (pushing existing columns at those
//           * positions one step down).
//           */
//          if (attributes.has(SEQUENCE)) {
//            int seq = attributes.getInt(SEQUENCE);
//            columnsOrderMap.putIfAbsent(seq, new ArrayList<>());
//            columnsOrderMap.get(seq).add(0, field.name());
//
//          } else {
//            while (columnsOrderMap.containsKey(colSequence)) {
//              colSequence++;
//            }
//            columnsOrderMap.putIfAbsent(colSequence, new ArrayList<>());
//            columnsOrderMap.get(colSequence).add(field.name());
//          }
//          columns.put(field.name(), column);
//          colSequence++;
//        }
//        result.put("columns", columns);
//
//        JSONArray columnsOrder = new JSONArray();
//        List<Integer> sequences = new ArrayList<>(columnsOrderMap.keySet());
//        sequences.sort(null);
//        for (int seq: sequences) {
//          for (String name: columnsOrderMap.get(seq)) {
//            columnsOrder.put(name);
//          }
//        }
//        result.put("columnsOrder", columnsOrder);
//        result.put("rowColumnsOrder", rowColumnsOrder);
//
//        resultType = new TypeInfo(result, fieldAttributes);
//        typeInfo.put(key, resultType);
//      }
//      if (colMetadata != null) {
//        colMetadata.putAll(resultType.fieldMetadata);
//      }
//      return resultType.type;
//    }
//  }
//
//  /**
//   * Converts a JSON value to a database-supported type.
//   */
//  public static Object fromJsonValue(Object value, Type toType, Database db) {
//    if (value == null) {
//      return null;
//
//    } else if (value == JSONObject.NULL) {
//      return null;
//
//    } else if (toType == Types.DateType
//        || toType == Types.TimeType
//        || toType == Types.DatetimeType) {
//
//      String s = value.toString();
//      if (s.startsWith("d")) {
//        /*
//         * Esql literal, return as-is
//         */
//        return s;
//      } else {
//        try {
//          return new Date(Instant.parse(value.toString()).toEpochMilli());
//        } catch (Exception pe) {
//          throw new IllegalArgumentException(value + " is not a valid date in the expected Javascript format");
//        }
//      }
//    } else {
//      return value;
//    }
//  }
//
//  /**
//   * Encode the (database) value as a JSON value.
//   */
//  public static Object toJsonValue(Object value) {
//    if (value == null) {
//      return JSONObject.NULL;
//
//    } else if (value instanceof Number) {
//      if (isIntegral((Number)value)) {
//        return value instanceof Long
//               ? ((Number)value).longValue()
//               : ((Number)value).intValue();
//      } else {
//        return ((Number)value).doubleValue();
//      }
//
//    } else if (value instanceof String) {
//      return value;
//
//    } else if (value instanceof Boolean
//        || value instanceof JSONArray
//        || value instanceof JSONObject) {
//      return value;
//
//    } else if (value instanceof Date) {
//      return TO_JAVASCRIPT_DATE_FORMAT.format((Date)value);
//
//    } else {
//      /*
//       * quote unsupported json types and expressions
//       */
//      return quote(String.valueOf(value));
//    }
//  }
//
//  private static Map<String, Object> forAttributes(Map<String, DbExpression> attributes) {
//    Map<String, Object> metadata = new HashMap<>();
//    for (Map.Entry<String, DbExpression> a: attributes.entrySet()) {
//      Object value = forExpression(a.getValue().expression());
//      metadata.put(a.getKey(), toJsonValue(value));
//    }
//    return metadata;
//  }
//
//  /*
//   * Different edit scenarios and the JSON data required to support it:
//   * 1. Edit of whole row
//   *
//   *   test: {
//   *     males: {
//   *       v: 5,
//   *       m: {
//   *         label: "Male employees",
//   *         maximum: null
//   *       }
//   *     },
//   *
//   *     females: {
//   *       v: 5,
//   *       m: {
//   *         label: "Female employees",
//   *         minimum: null
//   *       }
//   *     },
//   *
//   *     totalEmployees: {
//   *       v: null,
//   *       m: {
//   *         label: "Total employees",
//   *         show: false
//   *       }
//   *     },
//   *
//   *     turnover: {
//   *       v: 1000,
//   *       m: {
//   *         label: "Turnover",
//   *         required: true
//   *       }
//   *     },
//   *
//   *     productivity: {
//   *       v: null,
//   *       m: {
//   *         label: "Productivity",
//   *         required: true
//   *       }
//   *     }
//   *   },
//   *
//   *    // derived columns
//   *    this.$watch(() => this.test.males.value + this.test.females.value,
//   *                (newVal, oldVal) => this.test.totalEmployees.value = newVal,
//   *                {immediate: true});
//   *
//   *    this.$watch(() => this.test.turnover.value / (this.test.totalEmployees.value || 1),
//   *                (newVal, oldVal) => this.test.productivity.value = newVal,
//   *                {immediate: true});
//   *
//   *    // computed attributes
//   *    this.$watch(() => this.test.females.value,
//   *                (newVal, oldVal) => this.test.males.attributes.maximum = newVal,
//   *                {immediate: true});
//   *
//   *    this.$watch(() => this.test.males.value,
//   *                (newVal, oldVal) => this.test.females.attributes.minimum = newVal,
//   *                {immediate: true});
//   *
//   * 2. Edit of whole row broken into multiple steps with each step editing only a subset of the columns in the row
//   *
//   *
//   * 3. Edit of the whole row having a one-to-one association to an element of another row type
//   *
//   *
//   * 4. Edit of the whole row having a one-to-many association to an element of another row type
//   */
//
//  private static Object forExpression(Expression<?> expression) {
//    return forExpression(expression, "this.row", ".$v");
//  }
//
//  private static Object forExpression(Expression<?> expression, String qualifier, String suffix) {
//    Expression<?> qualified = ColumnRef.qualify(expression.copy(), qualifier, suffix, true);
//    Object value = qualified.value(JAVASCRIPT);
//    if (qualified instanceof Literal || qualified instanceof Symbol) {
//      return value;
//    } else {
//      /*
//       * Original expression was not a literal: mark it so that the
//       * client-side will recognise it as an expression and process
//       * it for reactivity (i.e. by adding it to a the watch-list).
//       */
//      return "${" + value.toString() + '}';
//    }
//  }
//
//  private static class TypeInfo {
//    public TypeInfo(JSONObject type, Map<String, Map<String, Object>> fieldMetadata) {
//      this.type = type;
//      this.fieldMetadata = fieldMetadata;
//    }
//
//    public final JSONObject type;
//    public final Map<String, Map<String, Object>> fieldMetadata;
//  }
//
//  /**
//   * Javascript send dates in ISO 8601 format (e.g. E.g. 2018-10-16T15:12:14.785Z)
//   * The following pattern is then used to convert them to Java date formats that can
//   * then be transferred to the database.
//   */
//  public static final SimpleDateFormat FROM_JAVASCRIPT_DATE_FORMAT =
//      new SimpleDateFormat("yyyy-MM-d'T'H:m:s.SX");
//
//  /**
//   * To send data to a Javascript client, ignore time zone as this is not kept in
//   * the database.
//   */
//  public static final SimpleDateFormat TO_JAVASCRIPT_DATE_FORMAT =
//      new SimpleDateFormat("yyyy-MM-d H:m:s.S");
//
//  /**
//   * Cache of static table type info.
//   * Todo remove table from this cache when altered
//   */
//  static Map<String, TypeInfo> typeInfo = new WeakHashMap<>();
}
