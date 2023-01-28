package ma.vi.esql.database.init;

import ma.vi.esql.builder.CreateStructBuilder;
import ma.vi.esql.database.Database;
import ma.vi.esql.semantic.type.Struct;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.define.struct.CreateStruct;

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
public class StructInitializer extends CreateInitializer<Struct,
                                                         CreateStruct,
                                                         CreateStructBuilder> {
  @Override
  protected CreateStructBuilder builder(Database db) {
    return new CreateStructBuilder(new Context(db.structure()));
  }

  @Override
  public Struct get(Database db, String name) {
    return db.structure().structExists(name)
         ? db.structure().struct(name)
         : null;
  }
}
