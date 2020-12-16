package ma.vi.esql.exec;

import ma.vi.esql.Databases;
import ma.vi.esql.database.HSqlDb;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.Program;
import org.junit.jupiter.api.Test;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class HSqlDbTest {
  @Test
  void create() {
    HSqlDb db = Databases.HSqlDb();
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
                           "  derived c a+b {" +
                           "    m1: a > 5," +
                           "    m2: a + b," +
                           "    m3: b > 5" +
                           "  }," +
                           "  derived d b+c {" +
                           "    m1: 10" +
                           "  }," +
                           "  e int {" +
                           "    m1: c" +
                           "  }," +
                           "  derived f (max(a) from S) {" +
                           "    m1: (min(a) from S)" +
                           "  }," +
                           "  derived g (distinct c from S where d>5) {" +
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
  }
}
