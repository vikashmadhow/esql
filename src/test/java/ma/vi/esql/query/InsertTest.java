/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.query;

import ma.vi.esql.DataTest;
import ma.vi.esql.builder.InsertBuilder;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.QueryParams;
import ma.vi.esql.exec.Result;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.modify.Insert;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static ma.vi.esql.syntax.Parser.Rules.INSERT;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class InsertTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> simpleInsert() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     Insert insert = p.parse(
                         "insert into S(_id, a, b, e, h, i) values "
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, 'Five'),"
                            + "(newid(), 6, 7, false, ['Nine', 'Neuf']text, 'Ten')",
                         INSERT);
                     Context context = new Context(db.structure());
                     assertEquals(new InsertBuilder(context)
                                        .in("S", null)
                                        .insertColumns("_id", "a", "b", "e", "h", "i")
                                        .insertRow("newid()", "1", "2", "true",  "['Four', 'Quatre']text", "'Five'")
                                        .insertRow("newid()", "6", "7", "false", "['Nine', 'Neuf']text",   "'Ten'")
                                        .build(),
                                  insert);
                     for (int i = 0; i < 10; i++) {
                       con.exec(insert);
                     }
                     Result rs = con.exec("select count(*) from S");
                     rs.toNext();
                     assertEquals(((Number)rs.get(1).value()).intValue(), 20);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> simpleInsertDateAndTime() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");

                     con.exec("insert into S(_id, a, n, o, p) values "
                                + "(newid(), 1, @d1, @t1, @dt1),"
                                + "(newid(), 2, @d2, @t2, @dt2),"
                                + "(newid(), 3, @d3, @t3, @dt3)",
                              new QueryParams()
                                .add("d1",  LocalDate.of(2012, 12, 12))
                                .add("t1",  LocalTime.of(11, 45, 0, 0))
                                .add("dt1", LocalDateTime.of(2012, 12, 12, 11, 45, 0, 0))

                                .add("d2",  LocalDate.of(1972, 11, 1))
                                .add("t2",  LocalTime.of(23, 1, 55, 0))
                                .add("dt2", LocalDateTime.of(1972, 11, 1, 23, 1, 55, 0))

                                .add("d3",  LocalDate.of(1845, 3, 21))
                                .add("t3",  LocalTime.of(19, 0, 12, 345_000_000))
                                .add("dt3", LocalDateTime.of(1845, 3, 21, 19, 0, 12, 345_000_000))

                     );

                     Result rs = con.exec("select n, o, p from S order by a");
                     rs.toNext();
                     assertEquals(LocalDate.of(2012, 12, 12), rs.value("n"));
                     assertEquals(LocalTime.of(11, 45, 0, 0), rs.value("o"));
                     assertEquals(LocalDateTime.of(2012, 12, 12, 11, 45, 0, 0), rs.value("p"));

                     rs.toNext();
                     assertEquals(LocalDate.of(1972, 11, 1), rs.value("n"));
                     assertEquals(LocalTime.of(23, 1, 55, 0), rs.value("o"));
                     assertEquals(LocalDateTime.of(1972, 11, 1, 23, 1, 55, 0), rs.value("p"));

                     rs.toNext();
                     assertEquals(LocalDate.of(1845, 3, 21), rs.value("n"));
                     assertEquals(LocalTime.of(19, 0, 12, 345_000_000), rs.value("o"));
                     assertEquals(LocalDateTime.of(1845, 3, 21, 19, 0, 12, 345_000_000), rs.value("p"));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> insertSpecialCharacters() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");

                     String v1 = """
                                 This is a test
                                 string containing
                                 multiple lines
                                 separated\tby\tcarriage returns
                                 and tabs.
                                 """;
                     con.exec("insert into S(_id, a, b, e, h, i) values "
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, @v1)",
                              new QueryParams().add("v1", v1));

                     Result rs = con.exec("select i from S order by a");
                     rs.toNext();
                     assertEquals(v1, rs.value(1));
                   }
                 }));
  }

 @TestFactory
  Stream<DynamicTest> insertionOfArrays() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");

                     UUID[] u1 = new UUID[]{ UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID() };
                     UUID[] u2 = new UUID[]{ UUID.randomUUID() };
                     UUID[] u3 = new UUID[0];
                     UUID[] u4 = null;

                     con.exec("insert into S(_id, a, b, e, h, i, m) values "
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, 'Five', @u1),"
                            + "(newid(), 2, 7, false, ['Nine', 'Neuf']text, 'Ten',   @u2),"
                            + "(newid(), 3, 2, true, ['Four', 'Quatre']text, 'Five', @u3),"
                            + "(newid(), 4, 7, false, ['Nine', 'Neuf']text, 'Ten',   @u4)",
                            new QueryParams().add("u1", u1)
                                             .add("u2", u2)
                                             .add("u3", u3)
                                             .add("u4", u4));

                     Result rs = con.exec("select a, h, m from S order by a");
                     rs.toNext(); assertArrayEquals(new String[]{"Four", "Quatre"}, rs.value("h"));
                                  assertArrayEquals(u1, rs.value("m"));
                     rs.toNext(); assertArrayEquals(new String[]{"Nine", "Neuf"},   rs.value("h"));
                                  assertArrayEquals(u2, rs.value("m"));
                     rs.toNext(); assertArrayEquals(new String[]{"Four", "Quatre"}, rs.value("h"));
                                  assertArrayEquals(u3, rs.value("m"));
                     rs.toNext(); assertArrayEquals(new String[]{"Nine", "Neuf"},   rs.value("h"));
                                  assertArrayEquals(u4, rs.value("m"));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> insertAndReadBool() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     UUID id1 = UUID.randomUUID(),
                          id2 = UUID.randomUUID(),
                          id3 = UUID.randomUUID();
                     con.exec("insert into S(_id, e) values "
                            + "(u'" + id1 + "', true),"
                            + "(u'" + id2 + "', false),"
                            + "(u'" + id3 + "', null)");
                     Result rs = con.exec("select e from S where _id=u'" + id1 + "'");
                     assertTrue(rs.toNext());
                     assertEquals(rs.get("e").value(), true);

                     rs = con.exec("select e from S where _id=u'" + id2 + "'");
                     assertTrue(rs.toNext());
                     assertEquals(rs.get("e").value(), false);

                     rs = con.exec("select _id, e from S where e");
                     assertTrue(rs.toNext());
                     assertEquals(rs.get("_id").value(), id1);

                     rs = con.exec("select _id, coalesce(e, false) from S where coalesce(e, false)=true");
                     assertTrue(rs.toNext());
                     assertEquals(rs.get("_id").value(), id1);

                     rs = con.exec("select not e from S where _id=u'" + id2 + "' and not e");
                     assertTrue(rs.toNext());
                     assertEquals(rs.get(1).value(), true);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> insertAndReadArray() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     Insert insert = p.parse(
                         "insert into S(_id, a, b, e, h, j) values "
                             + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                             + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)",
                         INSERT);
                     con.exec(insert);

                     Result rs = con.exec("select _id, a, b, e, h, j from S");
                     Set<List<String>> stringArray = new HashSet<>();
                     Set<List<Integer>> intArray = new HashSet<>();
                     rs.toNext(); stringArray.add(Arrays.asList(rs.value("h"))); intArray.add(Arrays.asList(rs.value("j")));
                     rs.toNext(); stringArray.add(Arrays.asList(rs.value("h"))); intArray.add(Arrays.asList(rs.value("j")));

                     assertEquals(Set.of(Arrays.asList("Four", "Quatre"),
                                         Arrays.asList("Nine", "Neuf", "X")), stringArray);

                     assertEquals(Set.of(Arrays.asList(1, 2, 3),
                                         Arrays.asList(5, 6, 7, 8)), intArray);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> insertFromSelect() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec(
                         "insert into S(_id, a, b, e, h, j) values "
                             + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                             + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     con.exec("insert into S(_id, a, b, e, h, j) "
                            + "select newid(), 7, b, e, h, j from S");

                     Result rs = con.exec("select _id, a, b, e, h, j from S order by a");

                     rs.toNext(); assertEquals(rs.get("a").value(), 1);
                     rs.toNext(); assertEquals(rs.get("a").value(), 6);
                     rs.toNext(); assertEquals(rs.get("a").value(), 7);
                     rs.toNext(); assertEquals(rs.get("a").value(), 7);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> insertWithSelectInValues() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec(
                         "insert into S(_id, a, b, e, h, j) values "
                             + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                             + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     con.exec("""
                              insert into a.b.T(_id, a, b, x, y, s_id)
                              values(newid(), 1, 2, 3, 4,  (select _id from S where a=1)),
                                    (newid(), 2, 2, 2, 2, (select _id from S where a=2)),
                                    (newid(), 7, 8, 9, 10, (select _id from S where a=6))
                              """);

                     Result rs = con.exec("select _id from S order by a");
                     rs.toNext(); UUID id1 = rs.value(1);
                     rs.toNext(); UUID id2 = rs.value(1);

                     rs = con.exec("select a, b, x, y, s_id from a.b.T order by a");
                     rs.toNext(); assertEquals((Integer)rs.value(1), 1);
                                assertEquals((Integer)rs.value(2), 2);
                                assertEquals((Integer)rs.value(3), 3);
                                assertEquals((Integer)rs.value(4), 4);
                                assertEquals(rs.value(5), id1);

                     rs.toNext(); assertEquals((Integer)rs.value(1), 2);
                                assertEquals((Integer)rs.value(2), 2);
                                assertEquals((Integer)rs.value(3), 2);
                                assertEquals((Integer)rs.value(4), 2);
                                assertNull(rs.value(5));

                     rs.toNext(); assertEquals((Integer)rs.value(1), 7);
                                assertEquals((Integer)rs.value(2), 8);
                                assertEquals((Integer)rs.value(3), 9);
                                assertEquals((Integer)rs.value(4), 10);
                                assertEquals(rs.value(5), id2);
                   }
                 }));
  }
}
