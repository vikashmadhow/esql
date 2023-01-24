package ma.vi.esql.init;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.init.TableInitializer;
import ma.vi.esql.semantic.type.BaseRelation;
import ma.vi.esql.semantic.type.Struct;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class TableInitTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> create() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   TableInitializer init = new TableInitializer();
                   init.add(db, TableInitTest.class.getResourceAsStream("/init/test_tables.yml"));

                   BaseRelation s1 = db.structure().relation("TestImportTable");
                   System.out.println(s1);

                   BaseRelation s2 = db.structure().relation("a.b.TestImportTable2");
                   System.out.println(s2);
                 }));
  }
}
