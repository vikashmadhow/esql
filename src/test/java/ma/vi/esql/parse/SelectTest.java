/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.Databases;
import ma.vi.esql.TestDatabase;
import ma.vi.esql.builder.Attr;
import ma.vi.esql.builder.SelectBuilder;
import ma.vi.esql.database.Database;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.Program;
import ma.vi.esql.parser.Statement;
import ma.vi.esql.parser.query.Select;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

import static ma.vi.esql.parser.Parser.Rules.SELECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class SelectTest {
  static Database[] databases;

  @BeforeAll
  static void setup() {
    databases = new Database[] {
        Databases.Postgresql(),
        Databases.SqlServer(),
        Databases.HSqlDb(),
    };

    for (Database db: databases) {
      System.out.println(db.target());
      Parser p = new Parser(db.structure());
      try (EsqlConnection con = db.esql(db.pooledConnection())) {
        Program s = p.parse("create table S drop undefined(" +
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
        con.exec(s);

        s = p.parse("create table a.b.T drop undefined(" +
                    "  {" +
                    "    name: 'T'," +
                    "    description: 'T test table'," +
                    "    tm1: (max(b) from T)," +
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
                    "  s_id uuid {" +
                    "    link_table: 'S', " +
                    "    link_table_code: '_id', " +
                    "    link_table_label: 'a' " +
                    "  }," +
                    "  foreign key(s_id) references S(_id)," +
                    "  primary key(_id)" +
                    ")");
        con.exec(s);
      }
    }
  }

  @TestFactory
  Stream<DynamicTest> select() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Select select = p.parse("select s.*, T.*, v:x.a + x.b {m1:x, m2: y >= 5} " +
                                             "  from s:S " +
                                             "  left join a.b.T on s._id=T.s_id " +
                                             "  join x:a.b.X on x.t_id=T._id " +
                                             " times y:b.Y " +
                                             " order by s.x desc," +
                                             "          y.x," +
                                             "          T.b asc", SELECT);

                     Context context = new Context(db.structure());
                     assertEquals(new SelectBuilder(context)
                                      .starColumn("s")
                                      .starColumn("T")
                                      .column("x.a+x.b", "v", Attr.of("m1", "x"), Attr.of("m2", "y>=5"))
                                      .from("S", "s")
                                      .leftJoin("a.b.T", null, "s._id = T.s_id")
                                      .join("a.b.X", "x", "x.t_id = T._id")
                                      .times("b.Y", "y")
                                      .orderBy("s.x", "desc")
                                      .orderBy("y.x")
                                      .orderBy("T.b", "asc")
                                      .build(),
                                  select);
                     con.exec(select);
                   }
                 }));
  }


  @Test
  void simpleSelect() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    Program program = parser.parse("select * from s:S left join a.b.T on s._id=t.s_id order by x desc");

    List<Statement<?, ?>> st = program.statements();
    assertEquals(1, st.size());

    assertTrue(st.get(0) instanceof Select);
    Select select = (Select)st.get(0);
    Context context = new Context(db.structure());
    assertEquals(new SelectBuilder(context)
                      .starColumn(null)
                      .from("S", "s")
                      .leftJoin("a.b.T", "T", "s._id = t.s_id")
                      .orderBy("x", "desc")
                      .build(),
                 select);
  }

  @Test
  void otherSelect() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    Program program = parser.parse("select s.*, T.*, v:x.a + x.b {m1:x, m2: y >= 5} " +
                                        "  from s:S " +
                                        "  left join a.b.T on s._id=T.s_id " +
                                        "  join x:a.b.X on x.t_id=T._id " +
                                        " times y:b.Y " +
                                        " order by s.x desc," +
                                        "          y.x," +
                                        "          T.b asc");

    List<Statement<?, ?>> st = program.statements();
    assertEquals(1, st.size());

    assertTrue(st.get(0) instanceof Select);
    Select select = (Select)st.get(0);
    Context context = new Context(db.structure());
    assertEquals(new SelectBuilder(context)
                      .starColumn("s")
                      .starColumn("T")
                      .column("x.a+x.b", "v", Attr.of("m1", "x"), Attr.of("m2", "y>=5"))
                      .from("S", "s")
                      .leftJoin("a.b.T", null, "s._id = T.s_id")
                      .join("a.b.X", "x", "x.t_id = T._id")
                      .times("b.Y", "y")
                      .orderBy("s.x", "desc")
                      .orderBy("y.x")
                      .orderBy("T.b", "asc")
                      .build(),
                 select);
  }
}
