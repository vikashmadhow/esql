package ma.vi.esql.database.init;

import ma.vi.esql.builder.Attr;
import ma.vi.esql.builder.CreateBuilder;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.define.Create;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An abstract initializer to create structs or tables from a hierarchical
 * representation (such as in a YAML file).
 * <p>
 * An example of the expected input format:
 * <pre>
 * struct_name:
 *   $metadata:
 *     name: Test import
 *     description: This is a test import
 *   id:
 *     type: int
 *     required: true
 *     unique: true
 *   first_name:
 *     type: string
 *     required: true
 *   last_name:
 *     type: string
 *     required: true
 *   name: "first_name || ' ' || last_name"
 *   phone:
 *     type: string
 *   address:
 *     type: text
 *   date_of_birth:
 *     type: date
 * </pre>
 *
 * @param <O> The type of objects created by this initializer.
 * @param <C> The creation statement required to create objects of type O.
 * @param <B> The builder for creating statement of type C.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class CreateInitializer<O,
                                        C extends Create,
                                        B extends CreateBuilder<C, B>>
                      implements Initializer<O> {

  abstract protected B builder(Database db);

  /**
   * Treat required as non-null. Default is true. If this is false, the column
   * is not set as non-null even when there is a required attribute set on it.
   * The attribute is, however, preserved and can be used to validate the column
   * without being enforced at the database level.
   */
  protected boolean requiredAsNull() {
    return true;
  }

  protected void processAttributes(B builder,
                                   String columnName,
                                   Map<String, String> attrs) {}

  @Override
  public O add(Database db,
               boolean  overwrite,
               String   name,
               O        existing,
               Map<String, Object> definition) {
    B builder = builder(db);
    builder.name(name);
    for (var e: definition.entrySet()) {
      String columnName = e.getKey();
      if (columnName.equals(METADATA)) {
        /*
         * Struct-level metadata.
         */
        Map<String, Object> columnDef = (Map<String, Object>)e.getValue();
        for (var def: columnDef.entrySet())
          builder.metadata(def.getKey(), def.getValue().toString());
      } else {
        /*
         * Columns.
         */
        boolean notNull = false;
        String expression = null;
        Map<String, String> attrs = new LinkedHashMap<>();
        String type = null;

        if (e.getValue() instanceof String ex) {
          expression = ex;
        } else {
          Map<String, Object> columnDef = (Map<String, Object>)e.getValue();
          for (var def: columnDef.entrySet()) {
            switch(def.getKey()) {
              case "type",
                   "_type"       -> type    = (String)def.getValue();
              case "required"    -> {
                if (requiredAsNull()) notNull = (Boolean)def.getValue();
                else                  attrs.put(def.getKey(), def.getValue().toString());
              }
              case "expression",
                   "_expression" -> expression = def.getValue().toString();
              default            -> attrs.put(def.getKey(), def.getValue().toString());
            }
          }
        }
        Attr[] attributes = attrs.entrySet().stream()
                                 .map(a -> new Attr(a.getKey(), removeExprMarks(a.getValue())))
                                 .toArray(Attr[]::new);
        if ((type == null || type.equals("#computed")) && expression != null) {
          builder.derivedColumn(columnName, expression, attributes);
        } else if (type == null) {
          throw new IllegalArgumentException("Type not specified for non-derived " + columnName);
        } else {
          builder.column(columnName, type, notNull, expression, attributes);
        }
        processAttributes(builder, columnName, attrs);
      }
    }
    try (EsqlConnection con = db.esql()) {
      con.exec((Esql<?, ?>)builder.build());
    }
    return get(db, name);
  }

  private static String removeExprMarks(String expr) {
    return expr == null          ? null
         : expr.startsWith("$(")
        && expr.endsWith(")")    ? expr.substring(2, expr.length() - 1)
         : expr;
  }

  protected static String removeQuotes(String text) {
    return text == null         ?  null
         : text.startsWith("'")
        && text.endsWith  ("'") ? text.substring(1, text.length() -1)
         : text;
  }
}
