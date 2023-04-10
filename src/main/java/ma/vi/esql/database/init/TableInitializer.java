package ma.vi.esql.database.init;

import ma.vi.esql.builder.CreateTableBuilder;
import ma.vi.esql.database.Database;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.define.Attribute;
import ma.vi.esql.syntax.define.table.CreateTable;
import ma.vi.esql.syntax.expression.ColumnRef;
import ma.vi.esql.syntax.expression.Expression;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
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
  public TableInitializer() {
    this(true);
  }

  public TableInitializer(boolean requiredAsNonNull) {
    this.requiredAsNonNull = requiredAsNonNull;
  }

  @Override
  protected boolean requiredAsNull() {
    return requiredAsNonNull;
  }

  @Override
  protected CreateTableBuilder builder(Database db) {
    return new CreateTableBuilder(new Context(db.structure())).dropUndefined(true);
  }

  @Override
  protected void processAttributes(CreateTableBuilder  builder,
                                   String              columnName,
                                   List<Attribute>     attributes) {
    Map<String, Attribute> attrs = attributes.stream()
                                             .collect(toMap(Attribute::name,
                                                            Function.identity()));
    if (attrs.containsKey(UNIQUE)) {
      builder.unique(columnName);
    }
    if (attrs.containsKey(PRIMARY_KEY)
     || columnName.equals("_id")) {
      builder.primaryKey(columnName);
    }
    if (attrs.containsKey(LINK_TABLE)
     && attrs.containsKey(LINK_CODE)) {
      builder.foreignKey(columnName,
                         attrs.get(LINK_TABLE).evaluateAttribute().toString(),
                         linkCode(attrs.get(LINK_CODE)));
    }
  }

  private static String linkCode(Attribute a) {
    Expression<?, ?> expr = a.attributeValue();
    return expr instanceof ColumnRef ref
         ? ref.columnName()
         : a.evaluateAttribute().toString();
  }

  @Override
  public BaseRelation get(Database db, String name) {
    return db.structure().relationExists(name)
         ? db.structure().relation(name)
         : null;
  }

  private final boolean requiredAsNonNull;
}
