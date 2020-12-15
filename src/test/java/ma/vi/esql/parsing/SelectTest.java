package ma.vi.esql.parsing;

import ma.vi.esql.Databases;
import ma.vi.esql.TestDatabase;
import ma.vi.esql.parser.*;
import ma.vi.esql.builder.Attr;
import ma.vi.esql.builder.SelectBuilder;
import ma.vi.esql.parser.query.Select;
import ma.vi.esql.type.Selection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SelectTest {
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
  void select() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    Program program = parser.parse("select s.*, T.*, v:x.a + x.b {m1:x, m2: y >= 5} " +
                                        "  from s:S " +
                                        "  left join a.b.T on s._id=T.s_id " +
                                        "  join x:a.b.X on x.t_id=T._id " +
                                        " cross y:b.Y " +
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
                      .cross("b.Y", "y")
                      .orderBy("s.x", "desc")
                      .orderBy("y.x")
                      .orderBy("T.b", "asc")
                      .build(),
                 select);
  }

  @Test
  void simpleSelectType() {
    TestDatabase db = Databases.TestDatabase();
    Parser parser = new Parser(db.structure());
    Program program = parser.parse("select * from S");

    List<Statement<?, ?>> st = program.statements();
    Select select = (Select)st.get(0);

    Selection type = select.type();
    System.out.println(type);
  }
}
