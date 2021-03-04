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
public class CreateTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> create() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Program s = p.parse("create table A drop undefined(" +
                                             "  {" +
                                             "    name: 'A'," +
                                             "    description: 'A test table'," +
                                             "    tm1: (max(b) from A)," +
                                             "    tm2: a > b" +
                                             "  }, " +
                                             "  _id uuid not null," +
                                             "  a int {" +
                                             "    m1: b > 5," +
                                             "    m2: 10," +
                                             "    m3: a != 0" +
                                             "  }," +
                                             "  b int {" +
                                             "    m1: b < 0" +
                                             "  }," +
                                             "  c=a+b {" +
                                             "    m1: a > 5," +
                                             "    m2: a + b," +
                                             "    m3: b > 5" +
                                             "  }," +
                                             "  d=b+c {" +
                                             "    m1: 10" +
                                             "  }," +
                                             "  e int {" +
                                             "    m1: c" +
                                             "  }," +
                                             "  f=(max(a) from A) {" +
                                             "    m1: (min(a) from A)" +
                                             "  }," +
                                             "  g=(distinct c from A where d>5) {" +
                                             "    m1: (min(a) from a.b.B)" +
                                             "  }," +
                                             "  h text {" +
                                             "    m1: 5" +
                                             "  }," +
                                             "  i string {" +
                                             "    label: (lv.label from lv:_platform.lookup.LookupValue" +
                                             "                     join l:_platform.lookup.Lookup on lv.lookup_id=l._id" +
                                             "                                  and l.name='City'" +
                                             "                    where lv.code=i)" +
                                             "  }," +
                                             "  primary key(_id)" +
                                             ")");
                     System.out.println(s);
                     con.exec(s);
                   }
                 }));
  }

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
