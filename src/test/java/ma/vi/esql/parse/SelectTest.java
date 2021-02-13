/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.DataTest;
import ma.vi.esql.Databases;
import ma.vi.esql.TestDatabase;
import ma.vi.esql.builder.Attr;
import ma.vi.esql.builder.SelectBuilder;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.parser.Context;
import ma.vi.esql.parser.Parser;
import ma.vi.esql.parser.Program;
import ma.vi.esql.parser.Statement;
import ma.vi.esql.parser.query.Select;
import ma.vi.esql.type.AmbiguousColumnException;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

import static ma.vi.esql.parser.Parser.Rules.SELECT;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class SelectTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> simpleSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Select select = p.parse("select a, b from s:S order by s.a asc", SELECT);
                     Context context = new Context(db.structure());
                     assertEquals(new SelectBuilder(context)
                                        .column("a", null)
                                        .column("b", null)
                                        .from("S", "s")
                                        .orderBy("s.a", "asc")
                                        .build(),
                                  select);
                     con.exec(select);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> simpleSelectFromSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(newid(), 1, 2, true, text['Four', 'Quatre'], int[1, 2, 3]),"
                            + "(newid(), 6, 7, false, text['Nine', 'Neuf', 'X'], int[5, 6, 7, 8])");

                     Select select = p.parse("select a {required: b < 5, mx: b > 5}, b "
                                           + "from x:(select a, b from s:S order by s.a asc)",
                                             SELECT);
                     Result rs = con.exec(select);
                     rs.next(); assertEquals(1, (Integer)rs.value(1));
                                assertEquals(2, (Integer)rs.value(2));
                     rs.next(); assertEquals(6, (Integer)rs.value(1));
                                assertEquals(7, (Integer)rs.value(2));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> simpleSelectFromSelectFromSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(newid(), 1, 2, true, text['Four', 'Quatre'], int[1, 2, 3]),"
                            + "(newid(), 6, 7, false, text['Nine', 'Neuf', 'X'], int[5, 6, 7, 8])");

                     Select select = p.parse(
                         "select a, b "
                       + "from x:(select a, b "
                               + "from x:(select a, b "
                                       + "from s:S order by s.a asc) order by x.a asc)", SELECT);
                     Result rs = con.exec(select);
                     rs.next(); assertEquals(1, (Integer)rs.value(1));
                                assertEquals(2, (Integer)rs.value(2));
                     rs.next(); assertEquals(6, (Integer)rs.value(1));
                                assertEquals(7, (Integer)rs.value(2));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> simpleSelectDerivedFromSelectFromSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(newid(), 1, 2, true, text['Four', 'Quatre'], int[1, 2, 3]),"
                            + "(newid(), 6, 7, false, text['Nine', 'Neuf', 'X'], int[5, 6, 7, 8])");

                     Select select = p.parse(
                         "select a, c "
                       + "from x:(select a, c "
                               + "from x:(select a, c "
                                       + "from s:S order by s.a asc) order by x.a asc)", SELECT);
                     Result rs = con.exec(select);
                     rs.next(); assertEquals(1,  (Integer)rs.value(1));
                                assertEquals(3,  (Integer)rs.value(2));
                     rs.next(); assertEquals(6,  (Integer)rs.value(1));
                                assertEquals(13, (Integer)rs.value(2));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> simpleSelectAllFromSelectFromSelectDerived() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(newid(), 1, 2, true, text['Four', 'Quatre'], int[1, 2, 3]),"
                            + "(newid(), 6, 7, false, text['Nine', 'Neuf', 'X'], int[5, 6, 7, 8])");

                     Select select = p.parse(
                         "select * "
                       + "from x:(select * "
                               + "from x:(select a, c "
                                       + "from s:S order by s.a asc) order by x.a asc)", SELECT);
                     Result rs = con.exec(select);
                     rs.next(); assertEquals(1,  (Integer)rs.value(1));
                                assertEquals(3,  (Integer)rs.value(2));
                     rs.next(); assertEquals(6,  (Integer)rs.value(1));
                                assertEquals(13, (Integer)rs.value(2));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> simpleSelectDerivedFromSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(newid(), 1, 2, true, text['Four', 'Quatre'], int[1, 2, 3]),"
                            + "(newid(), 6, 7, false, text['Nine', 'Neuf', 'X'], int[5, 6, 7, 8])");

                     Select select = p.parse("select a, c from x:(select a, c from s:S order by s.a asc)", SELECT);
                     Result rs = con.exec(select);
                     rs.next(); assertEquals(1, (Integer)rs.value(1));
                                assertEquals(3, (Integer)rs.value(2));
                     rs.next(); assertEquals(6, (Integer)rs.value(1));
                                assertEquals(13, (Integer)rs.value(2));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> selectDerivedColumn() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Select select = p.parse("select a, b, c from s:S where c > 1000 order by c asc, a desc", SELECT);
                     Context context = new Context(db.structure());
                     assertEquals(new SelectBuilder(context)
                                        .column("a", null)
                                        .column("b", null)
                                        .column("c", null)
                                        .from("S", "s")
                                        .where("c>1000")
                                        .orderBy("c", "asc")
                                        .orderBy("a", "desc")
                                        .build(),
                                  select);
                     con.exec(select);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> joinSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     assertThrows(AmbiguousColumnException.class,
                                  () -> con.exec("select a, b " +
                                                     "  from s:S " +
                                                     "  left join a.b.T on s._id=T.s_id " +
                                                     "  join x:a.b.X on x.t_id=T._id " +
                                                     " times y:b.Y " +
                                                     " order by s.a desc," +
                                                     "          y.b," +
                                                     "          T.b asc"));
                     con.exec(
                         "select s.a, T.b, T.c, s.c, s.a + T.c + s.d " +
                         "  from s:S " +
                         "  left join a.b.T on s._id=T.s_id " +
                         "  join x:a.b.X on x.t_id=T._id " +
                         " times y:b.Y " +
                         " order by s.a desc," +
                         "          y.b," +
                         "          T.b asc");
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> joinStarSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Select select = p.parse("select s.*, T.*, v:x.a + y.b {m1:x.a, m2: y.b >= 5} " +
                                             "  from s:S " +
                                             "  left join a.b.T on s._id=T.s_id " +
                                             "  join x:a.b.X on x.t_id=T._id " +
                                             " times y:b.Y " +
                                             " order by s.a desc," +
                                             "          y.b," +
                                             "          T.b asc", SELECT);

                     Context context = new Context(db.structure());
                     assertEquals(new SelectBuilder(context)
                                      .starColumn("s")
                                      .starColumn("T")
                                      .column("x.a+y.b", "v", Attr.of("m1", "x.a"), Attr.of("m2", "y.b>=5"))
                                      .from("S", "s")
                                      .leftJoin("a.b.T", null, "s._id = T.s_id")
                                      .join("a.b.X", "x", "x.t_id = T._id")
                                      .times("b.Y", "y")
                                      .orderBy("s.a", "desc")
                                      .orderBy("y.b")
                                      .orderBy("T.b", "asc")
                                      .build(),
                                  select);
                     con.exec(select);
                   }
                 }));
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
