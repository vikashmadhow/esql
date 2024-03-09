package ma.vi.esql.init;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.init.table.StructInitializer;
import ma.vi.esql.semantic.type.Struct;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class StructInitTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> create() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   StructInitializer init = new StructInitializer();
                   init.add(db, "/init/test_structs.yml");

                   Struct s1 = db.structure().struct("TestImport");
                   System.out.println(s1);

                   Struct s2 = db.structure().struct("a.b.TestImport2");
                   System.out.println(s2);
                 }));
  }
}
