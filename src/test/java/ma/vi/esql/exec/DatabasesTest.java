package ma.vi.esql.exec;

import ma.vi.esql.Databases;
import ma.vi.esql.database.Database;
import ma.vi.esql.parser.CircularReferenceException;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.Program;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DatabasesTest {
  static Database[] databases;

  @BeforeAll
  static void setup() {
    databases = new Database[] {
        Databases.Postgresql(),
        Databases.HSqlDb(),
        Databases.SqlServer()
    };
  }

  @TestFactory
  Stream<DynamicTest> create() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Program s = p.parse("create table S (" +
                                             "  {" +
                                             "    name: 'S'," +
                                             "    description: 'S test table'," +
                                             "    tm1: (max(b) from S)," +
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
                                             "  f=(max(a) from S) {" +
                                             "    m1: (min(a) from S)" +
                                             "  }," +
                                             "  g=(distinct c from S where d>5) {" +
                                             "    m1: (min(a) from a.b.T)" +
                                             "  }," +
                                             "  h int {" +
                                             "    m1: 5" +
                                             "  }," +
                                             "  i string {" +
                                             "    label: (lv.label from lv:LookupValue" +
                                             "                     join l:Lookup on lv.lookup_id=l._id" +
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
                    try (EsqlConnection con = db.esql(db.pooledConnection())) {
                      Program s = p.parse("create table X (" +
                                              "  _id int, " +
                                              "  a=a" +
                                              ")");
                      System.out.println(s);
                      Assertions.assertThrows(
                          CircularReferenceException.class,
                          () -> con.exec(s));
                    }
                  }));
  }

  @TestFactory
  Stream<DynamicTest> mutuallyCircularDerivedColumn() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Program s = p.parse("create table X (" +
                                             "  _id int, " +
                                             "  a=b, " +
                                             "  b=a" +
                                             ")");
                     System.out.println(s);
                     Assertions.assertThrows(
                         CircularReferenceException.class,
                         () -> con.exec(s));
                   }
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
