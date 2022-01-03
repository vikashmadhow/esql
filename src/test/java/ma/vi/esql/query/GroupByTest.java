/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.query;

import ma.vi.esql.DataTest;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class GroupByTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> simpleGroupBy() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete T from a.b.T");
                     con.exec("delete s from s:S");

                     UUID id1 = UUID.randomUUID(), id2 = UUID.randomUUID();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(u'" + id1 + "', 1, 2, true, text['Four', 'Quatre'], int[1, 2, 3]),"
                                  + "(u'" + id2 + "', 6, 7, false, text['Nine', 'Neuf', 'X'], int[5, 6, 7, 8])");

                     con.exec("insert into a.b.T(_id, a, b, s_id) values"
                                  + "(newid(), 1, 2, u'" + id1 + "'), "
                                  + "(newid(), 6, 2, u'" + id1 + "'), "
                                  + "(newid(), 3, 4, u'" + id2 + "')");

                     Result rs = con.exec(
                         "select t.a, t.b, t.a * t.b, " +
                         "       sum(s.c if 0 < t.b < 5 else s.a)"
                       + "  from t:a.b.T join s:S on t.s_id=s._id "
                       + " group by 1, t.b, 3"
                       + " order by t.a desc");
                     printResult(rs, 10);

                     rs = con.exec(
                         "select t.a * t.b, " +
                         "       sum(s.c if 0 < t.b < 5 else s.a)"
                       + "  from t:a.b.T join s:S on t.s_id=s._id "
                       + " group by 1 "
                       + " order by 1 desc");
                     printResult(rs, 10);

//                     rs.next(); assertEquals(1, (Integer)rs.value("a"));
//                                assertEquals(2, (Integer)rs.value("b"));
//                                assertEquals(3, (Integer)rs.value("c"));
//                     rs.next(); assertEquals(3, (Integer)rs.value("a"));
//                                assertEquals(4, (Integer)rs.value("b"));
//                                assertEquals(13, (Integer)rs.value("c"));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> cubeGroupBy() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete T from a.b.T");
                     con.exec("delete s from s:S");

                     UUID id1 = UUID.randomUUID(), id2 = UUID.randomUUID();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(u'" + id1 + "', 1, 2, true, text['Four', 'Quatre'], int[1, 2, 3]),"
                                  + "(u'" + id2 + "', 6, 7, false, text['Nine', 'Neuf', 'X'], int[5, 6, 7, 8])");

                     con.exec("insert into a.b.T(_id, a, b, s_id) values"
                                  + "(newid(), 1, 2, u'" + id1 + "'), "
                                  + "(newid(), 6, 2, u'" + id1 + "'), "
                                  + "(newid(), 3, 4, u'" + id2 + "')");

                     Result rs = con.exec(
                         "select t.a, t.b, t.a * t.b, " +
                         "       sum(s.c if 0 < t.b < 5 else s.a)"
                       + "  from t:a.b.T join s:S on t.s_id=s._id "
                       + " group by cube(1, t.b, 3)"
                       + " order by t.a desc");
                     printResult(rs, 10);

                     rs = con.exec(
                         "select t.a * t.b, " +
                         "       sum(s.c if 0 < t.b < 5 else s.a)"
                       + "  from t:a.b.T join s:S on t.s_id=s._id "
                       + " group by cube(1) "
                       + " order by 1 desc");
                     printResult(rs, 10);

//                     rs.next(); assertEquals(1, (Integer)rs.value("a"));
//                                assertEquals(2, (Integer)rs.value("b"));
//                                assertEquals(3, (Integer)rs.value("c"));
//                     rs.next(); assertEquals(3, (Integer)rs.value("a"));
//                                assertEquals(4, (Integer)rs.value("b"));
//                                assertEquals(13, (Integer)rs.value("c"));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> rollupGroupBy() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete T from a.b.T");
                     con.exec("delete s from s:S");

                     UUID id1 = UUID.randomUUID(), id2 = UUID.randomUUID();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(u'" + id1 + "', 1, 2, true, text['Four', 'Quatre'], int[1, 2, 3]),"
                                  + "(u'" + id2 + "', 6, 7, false, text['Nine', 'Neuf', 'X'], int[5, 6, 7, 8])");

                     con.exec("insert into a.b.T(_id, a, b, s_id) values"
                                  + "(newid(), 1, 2, u'" + id1 + "'), "
                                  + "(newid(), 6, 2, u'" + id1 + "'), "
                                  + "(newid(), 3, 4, u'" + id2 + "')");

                     Result rs = con.exec(
                         "select t.a, t.b, t.a * t.b, " +
                         "       sum(s.c if 0 < t.b < 5 else s.a)"
                       + "  from t:a.b.T join s:S on t.s_id=s._id "
                       + " group by rollup(1, t.b, 3)"
                       + " order by t.a desc");
                     printResult(rs, 10);

                     rs = con.exec(
                         "select t.a * t.b, " +
                         "       sum(s.c if 0 < t.b < 5 else s.a)"
                       + "  from t:a.b.T join s:S on t.s_id=s._id "
                       + " group by rollup(1) "
                       + " order by 1 desc");
                     printResult(rs, 10);

//                     rs.next(); assertEquals(1, (Integer)rs.value("a"));
//                                assertEquals(2, (Integer)rs.value("b"));
//                                assertEquals(3, (Integer)rs.value("c"));
//                     rs.next(); assertEquals(3, (Integer)rs.value("a"));
//                                assertEquals(4, (Integer)rs.value("b"));
//                                assertEquals(13, (Integer)rs.value("c"));
                   }
                 }));
  }
}
