package ma.vi.esql.database.init;

import ma.vi.esql.builder.Attr;
import ma.vi.esql.builder.CreateStructBuilder;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.semantic.type.Struct;
import ma.vi.esql.syntax.Context;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An initializer to create structs from a hierarchical representation (such as
 * in a YAML file).
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
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class StructInitializer implements Initializer<Struct> {
  @Override
  public Struct add(Database db,
                    boolean  overwrite,
                    String   name,
                    Struct   existing,
                    Map<String, Object> definition) {
    CreateStructBuilder builder = new CreateStructBuilder(new Context(db.structure()));
    builder.name(name);
    for (var e: definition.entrySet()) {
      String columnName = e.getKey();
      if (columnName.equals("$metadata")) {
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
        Map<String, String> attrs =new LinkedHashMap<>();
        String type = null;

        if (e.getValue() instanceof String ex) {
          expression = ex;
        } else {
          Map<String, Object> columnDef = (Map<String, Object>)e.getValue();
          for (var def: columnDef.entrySet()) {
            switch(def.getKey()) {
              case "type"       -> type    = (String)def.getValue();
              case "required"   -> notNull = (Boolean)def.getValue();
              case "expression" -> expression = def.getValue().toString();
              default           -> attrs.put(def.getKey(), def.getValue().toString());
            }
          }
        }
        Attr[] attributes = attrs.entrySet().stream()
                                 .map(a -> new Attr(a.getKey(), a.getValue()))
                                 .toArray(Attr[]::new);
        if (type == null && expression != null) {
          builder.derivedColumn(columnName, expression, attributes);
        } else if (type == null) {
          throw new IllegalArgumentException("Type not specified for non-derived " + columnName);
        } else {
          builder.column(columnName, type, notNull, expression, attributes);
        }
      }
    }
    try (EsqlConnection con = db.esql()) {
      con.exec(builder.build());
    }
    return get(db, name);
  }

  @Override
  public Struct get(Database db, String name) {
    return db.structure().structExists(name)
         ? db.structure().struct(name)
         : null;
  }
}