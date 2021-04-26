package ma.vi.esql.parse;

import ma.vi.esql.DataTest;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.Program;
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
                                             "  i string," +
                                             "  primary key(_id)" +
                                             ")");
                     System.out.println(s);
                     con.exec(s);
                   }
                 }));
  }
}
