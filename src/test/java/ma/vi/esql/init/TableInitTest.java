package ma.vi.esql.init;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
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
                   try(EsqlConnection c = db.esql()) {
                     c.exec("drop table TestImportTable");
                     c.exec("drop table a.b.TestImportTable2");

                     TableInitializer init = new TableInitializer();
                     init.add(db, null, getClass().getResourceAsStream("/init/test_tables.yml"));

                     BaseRelation s1 = db.structure().relation("TestImportTable");
                     System.out.println(s1);

                     BaseRelation s2 = db.structure().relation("a.b.TestImportTable2");
                     System.out.println(s2);
                   }
                 }));
  }
}
