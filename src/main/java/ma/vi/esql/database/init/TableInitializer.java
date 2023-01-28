package ma.vi.esql.database.init;

import ma.vi.esql.builder.CreateTableBuilder;
import ma.vi.esql.database.Database;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.define.table.CreateTable;

import java.util.Map;

import static ma.vi.esql.builder.Attributes.*;

/**
 * An initializer to create tables from a hierarchical representation (such as
 * in a YAML file).
 * <p>
 * An example of the expected input format:
 * <pre>
 * table_name:
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
public class TableInitializer extends CreateInitializer<BaseRelation,
                                                        CreateTable,
                                                        CreateTableBuilder> {
  @Override
  protected CreateTableBuilder builder(Database db) {
    return new CreateTableBuilder(new Context(db.structure()));
  }

  @Override
  protected void processAttributes(CreateTableBuilder  builder,
                                   String              columnName,
                                   Map<String, String> attrs) {
    if (attrs.containsKey(UNIQUE)) {
      builder.unique(columnName);
    }
    if (attrs.containsKey(LINK_TABLE)
      && attrs.containsKey(LINK_CODE)) {
      builder.foreignKey(columnName,
                         removeQuotes(attrs.get(LINK_TABLE)),
                         removeQuotes(attrs.get(LINK_CODE)));
    }
  }

  @Override
  public BaseRelation get(Database db, String name) {
    return db.structure().relationExists(name)
         ? db.structure().relation(name)
         : null;
  }

  private static String removeQuotes(String text) {
    return text == null         ?  null
         : text.startsWith("'")
        && text.endsWith  ("'") ? text.substring(1, text.length() -1)
         : text;
  }
}
