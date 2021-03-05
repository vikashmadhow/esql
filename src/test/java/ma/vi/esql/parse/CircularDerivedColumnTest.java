package ma.vi.esql.parse;

import ma.vi.esql.DataTest;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.parser.CircularReferenceException;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.Program;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CircularDerivedColumnTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> circularDerivedColumn() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   Assertions.assertThrows(
                       CircularReferenceException.class,
                       () -> p.parse("create table X ("
                                         + "  _id int, "
                                         + "  a=a"
                                         + ")"));
                 }));
  }

  @TestFactory
  Stream<DynamicTest> mutuallyCircularDerivedColumn() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   Assertions.assertThrows(
                       CircularReferenceException.class,
                       () -> p.parse("create table X (" +
                                         "  _id int, " +
                                         "  a=b, " +
                                         "  b=a" +
                                         ")"));
                 }));
  }

  @TestFactory
  Stream<DynamicTest> nonCircularDerivedColumn() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Program s = p.parse("create table Y (" +
                                             "  _id int, " +
                                             "  a int, " +
                                             "  b=a + a, " +
                                             "  c=b + a, " +
                                             "  d=c + c + b " +
                                             ")");
                     con.exec(s);
                     System.out.println(s);
                   }
                 }));
  }
}
