/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.query;

import ma.vi.esql.DataTest;
import ma.vi.esql.Databases;
import ma.vi.esql.TestDatabase;
import ma.vi.esql.builder.Attr;
import ma.vi.esql.builder.SelectBuilder;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.ResultColumn;
import ma.vi.esql.semantic.type.AmbiguousColumnException;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.Program;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.syntax.expression.literal.UuidLiteral;
import ma.vi.esql.syntax.query.Select;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static ma.vi.esql.syntax.Parser.Rules.SELECT;
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
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                                  + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Select select = p.parse("select a, b from s:S order by s.a asc", SELECT);
                     Context context = new Context(db.structure());
                     assertEquals(new SelectBuilder(context)
                                        .column("a", "a")
                                        .column("b", "b")
                                        .from("S", "s")
                                        .orderBy("s.a", "asc")
                                        .build(),
                                  select);

                     Result rs = con.exec(select);
//                     printResult(rs, 20, true);
                     rs.toNext();
                     ResultColumn<Integer> c1 = rs.get(1);
                     ResultColumn<Integer> c2 = rs.get(2);
                     assertEquals(1,     c1.value());
                     assertEquals(false, c1.metadata().get("m1"));
                     assertEquals(10L,   c1.metadata().get("m2"));
                     assertEquals(true,  c1.metadata().get("m3"));

                     assertEquals(2,     c2.value());
                     assertEquals(false, c2.metadata().get("m1"));

                     rs.toNext(); c1 = rs.get(1); c2 = rs.get(2);
                     assertEquals(6,     c1.value());
                     assertEquals(true,  c1.metadata().get("m1"));
                     assertEquals(10L,   c1.metadata().get("m2"));
                     assertEquals(true,  c1.metadata().get("m3"));

                     assertEquals(7,     c2.value());
                     assertEquals(false, c2.metadata().get("m1"));
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
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Select select = p.parse("select a {required: b < 5, mx: b > 5}, b "
                                           + "from x:(select a, b from s:S order by s.a asc)",
                                             SELECT);

                     Result rs = con.exec(select);
                     // printResult(rs, 30, true);
                     rs.toNext();
                     ResultColumn<Integer> c1 = rs.get(1);
                     ResultColumn<Integer> c2 = rs.get(2);
                     assertEquals(1,     c1.value());
                     assertEquals(false, c1.metadata().get("m1"));
                     assertEquals(10L,   c1.metadata().get("m2"));
                     assertEquals(true,  c1.metadata().get("m3"));
                     assertEquals(true,  c1.metadata().get("required"));
                     assertEquals(false, c1.metadata().get("mx"));

                     assertEquals(2,     c2.value());
                     assertEquals(false, c2.metadata().get("m1"));

                     rs.toNext(); c1 = rs.get(1); c2 = rs.get(2);
                     assertEquals(6,     c1.value());
                     assertEquals(true,  c1.metadata().get("m1"));
                     assertEquals(10L,   c1.metadata().get("m2"));
                     assertEquals(true,  c1.metadata().get("m3"));
                     assertEquals(false, c1.metadata().get("required"));
                     assertEquals(true,  c1.metadata().get("mx"));

                     assertEquals(7,     c2.value());
                     assertEquals(false, c2.metadata().get("m1"));
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
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Select select = p.parse(
                         "select a, b "
                       + "from x:(select a, b "
                               + "from x:(select a, b "
                                       + "from s:S order by s.a asc) order by x.a asc)", SELECT);
                     Result rs = con.exec(select);
                     rs.toNext(); assertEquals(1, (Integer)rs.value(1));
                                assertEquals(2, (Integer)rs.value(2));
                     rs.toNext(); assertEquals(6, (Integer)rs.value(1));
                                assertEquals(7, (Integer)rs.value(2));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> selectExists() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Select select = p.parse(
                         "select a, b "
                       + "from x:(select a, b "
                               + "from x:(select a, b "
                                       + "from s:S order by s.a asc) order by x.a asc)" +
                             "where exists(select * from S)", SELECT);
                     Result rs = con.exec(select);
                     rs.toNext(); assertEquals(1, (Integer)rs.value(1));
                                assertEquals(2, (Integer)rs.value(2));
                     rs.toNext(); assertEquals(6, (Integer)rs.value(1));
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
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Select select = p.parse(
                         "select a, c "
                       + "from x:(select a, c "
                               + "from x:(select a, c "
                                       + "from s:S order by s.a asc) order by x.a asc)", SELECT);
                     Result rs = con.exec(select);
                     rs.toNext(); assertEquals(1, (Integer)rs.value(1));
                                assertEquals(3,  (Integer)rs.value(2));
                     rs.toNext(); assertEquals(6, (Integer)rs.value(1));
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
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Select select = p.parse(
                         "select * "
                       + "from x:(select * "
                               + "from x:(select a, c "
                                       + "from s:S order by s.a asc)) order by x.a asc", SELECT);
                     Result rs = con.exec(select);
                     rs.toNext(); assertEquals(1,  (Integer)rs.value(1));
                                  assertEquals(3,  (Integer)rs.value(2));
                     rs.toNext(); assertEquals(6,  (Integer)rs.value(1));
                                  assertEquals(13, (Integer)rs.value(2));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> iterationOverSelectResult() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Select select = p.parse(
                         "select * "
                       + "from x:(select * "
                               + "from x:(select a, c "
                                       + "from s:S order by s.a asc)) order by x.a asc", SELECT);

                     int i = 0;
                     for (Result.Row r: (Result)con.exec(select)) {
                       if (i == 0) {
                         assertEquals(1, (Integer)r.value(1));
                         assertEquals(3, (Integer)r.value(2));
                       } else {
                         assertEquals(6,  (Integer)r.value(1));
                         assertEquals(13, (Integer)r.value(2));
                       }
                       i++;
                     }
                     assertEquals(2, i);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> selectAllFromSelectAll() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Select select = p.parse("select * from x:(select * from S) where a >= 3 order by a", SELECT);
                     Result rs = con.exec(select);
                     rs.next(); assertInstanceOf(UUID.class, rs.value("_id"));
                                assertEquals(6,  (Integer)rs.value("a"));
                                assertEquals(7,  (Integer)rs.value("b"));
                                assertEquals(13, (Integer)rs.value("c"));
                                assertEquals(20, (Integer)rs.value("d"));
                                assertEquals(false, rs.value("e"));
                                assertEquals(6,  (Integer)rs.value("f"));
                                assertEquals(13, (Integer)rs.value("g"));
                                assertArrayEquals(new String[]{"Nine", "Neuf", "X"}, rs.value("h"));
                                assertEquals("Aie", rs.value("i"));
                                assertArrayEquals(new Integer[]{5,6,7,8}, rs.value("j"));
                                assertNull(rs.value("k"));
                                assertNull(rs.value("l"));
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
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Select select = p.parse("select a, c from x:(select a, c from s:S order by s.a asc)", SELECT);
                     Result rs = con.exec(select);
                     rs.toNext(); assertEquals(1, (Integer)rs.value(1));
                                  assertEquals(3, (Integer)rs.value(2));
                     rs.toNext(); assertEquals(6, (Integer)rs.value(1));
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
                                        .column("a", "a")
                                        .column("b", "b")
                                        .column("c", "c")
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
                     Select select = p.parse("""
                                                 select s.*, T.*, v:x.a + y.b {m1:x.a, m2: y.b >= 5}
                                                   from s:S
                                                   left join a.b.T on s._id=T.s_id
                                                   join x:a.b.X on x.t_id=T._id
                                                  times y:b.Y
                                                  where left(s.i, 2)='PI'
                                                  order by s.a desc,
                                                           y.b,
                                                           T.b asc""", SELECT);

                     Context context = new Context(db.structure());
                     assertEquals(UuidLiteral.PATTERN.matcher(
                                    new SelectBuilder(context)
                                      .starColumn("s")
                                      .starColumn("T")
                                      .column("x.a+y.b", "v", Attr.of("m1", "x.a"), Attr.of("m2", "y.b>=5"))
                                      .from("S", "s")
                                      .leftJoin("a.b.T", null, "s._id = T.s_id", false)
                                      .join("a.b.X", "x", "x.t_id = T._id", false)
                                      .times("b.Y", "y")
                                      .where("left(s.i, 2)='PI'")
                                      .orderBy("s.a", "desc")
                                      .orderBy("y.b")
                                      .orderBy("T.b", "asc")
                                      .build().toString()).replaceAll("00000000-0000-0000-0000-000000000000"),
                                  UuidLiteral.PATTERN.matcher(select.toString()).replaceAll("00000000-0000-0000-0000-000000000000"));
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

    List<? extends Expression<?, ?>> st = program.expressions();
    assertEquals(1, st.size());

    assertTrue(st.get(0) instanceof Select);
    Select select = (Select)st.get(0);
    Context context = new Context(db.structure());
    assertEquals(UuidLiteral.PATTERN.matcher(
                  new SelectBuilder(context)
                      .starColumn("s")
                      .starColumn("T")
                      .column("x.a+x.b", "v", Attr.of("m1", "x"), Attr.of("m2", "y>=5"))
                      .from("S", "s")
                      .leftJoin("a.b.T", null, "s._id = T.s_id", false)
                      .join("a.b.X", "x", "x.t_id = T._id", false)
                      .times("b.Y", "y")
                      .orderBy("s.x", "desc")
                      .orderBy("y.x")
                      .orderBy("T.b", "asc")
                      .build().toString()).replaceAll("00000000-0000-0000-0000-000000000000"),
                 UuidLiteral.PATTERN.matcher(select.toString()).replaceAll("00000000-0000-0000-0000-000000000000"));
  }
}
