package ma.vi.esql.query.define;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.filter.ComposableFilterTest;
import ma.vi.esql.syntax.Parser;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class IndexTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> create() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     ComposableFilterTest.setupTables(con);
                     con.exec("create unique index test_px_a on test.pX(a)");
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> drop() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     ComposableFilterTest.setupTables(con);
                     con.exec("create unique index test_px_a on test.pX(a)");
                     con.exec("drop index test_px_a on test.pX");
                   }
                 }));
  }
}
