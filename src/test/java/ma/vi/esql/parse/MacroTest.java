/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.QueryParams;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.composable.ComposableFilter;
import ma.vi.esql.semantic.type.AmbiguousColumnException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class MacroTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> binOnColumnOneTable() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete T from a.b.T");
                     con.exec("delete s from s:S");

                     UUID id1 = randomUUID(),
                          id2 = randomUUID(),
                          id3 = randomUUID(),
                          id4 = randomUUID(),
                          id5 = randomUUID(),
                          id6 = randomUUID();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(u'" + id1 + "', 1, 10, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(u'" + id2 + "', 2, 21, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(u'" + id3 + "', 3, 32, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(u'" + id4 + "', 4, 24, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(u'" + id5 + "', 5, 24, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(u'" + id6 + "', 6, 71, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     con.exec("insert into a.b.T(_id, a, b, s_id) values"
                            + "(newid(), 11, 2, u'" + id1 + "'), "
                            + "(newid(), 25, 4, u'" + id1 + "'), "
                            + "(newid(), 36, 6, u'" + id1 + "'), "
                            + "(newid(), 73, 2, u'" + id2 + "'), "
                            + "(newid(), 62, 4, u'" + id2 + "'), "
                            + "(newid(), 55, 7, u'" + id2 + "'), "
                            + "(newid(), 37, 8, u'" + id2 + "'), "
                            + "(newid(), 76, 9, u'" + id3 + "'), "
                            + "(newid(), 93, 1, u'" + id3 + "'), "
                            + "(newid(), 82, 2, u'" + id3 + "'), "
                            + "(newid(), 63, 5, u'" + id4 + "'), "
                            + "(newid(), 15, 6, u'" + id4 + "'), "
                            + "(newid(), 27, 7, u'" + id5 + "'), "
                            + "(newid(), 34, 8, u'" + id5 + "'), "
                            + "(newid(), 72, 9, u'" + id5 + "'), "
                            + "(newid(), 42, 2, u'" + id5 + "'), "
                            + "(newid(), 23, 3, u'" + id6 + "'), "
                            + "(newid(), 75, 5, u'" + id6 + "'), "
                            + "(newid(), 47, 7, u'" + id6 + "'), "
                            + "(newid(), 19, 8, u'" + id6 + "')");

                     Result rs = con.exec("select a, b, c, a*b, bin(a*c, 'a times c', 10, 25, 40, 75, 100, 200) from S order by a");
                     printResult(rs, 10);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> filterOnBin() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete T from a.b.T");
                     con.exec("delete s from s:S");

                     UUID id1 = randomUUID(),
                          id2 = randomUUID(),
                          id3 = randomUUID(),
                          id4 = randomUUID(),
                          id5 = randomUUID(),
                          id6 = randomUUID();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(u'" + id1 + "', 1, 10, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(u'" + id2 + "', 2, 21, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(u'" + id3 + "', 3, 32, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(u'" + id4 + "', 4, 24, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(u'" + id5 + "', 5, 24, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(u'" + id6 + "', 6, 71, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     con.exec("insert into a.b.T(_id, a, b, s_id) values"
                            + "(newid(), 11, 2, u'" + id1 + "'), "
                            + "(newid(), 25, 4, u'" + id1 + "'), "
                            + "(newid(), 36, 6, u'" + id1 + "'), "
                            + "(newid(), 73, 2, u'" + id2 + "'), "
                            + "(newid(), 62, 4, u'" + id2 + "'), "
                            + "(newid(), 55, 7, u'" + id2 + "'), "
                            + "(newid(), 37, 8, u'" + id2 + "'), "
                            + "(newid(), 76, 9, u'" + id3 + "'), "
                            + "(newid(), 93, 1, u'" + id3 + "'), "
                            + "(newid(), 82, 2, u'" + id3 + "'), "
                            + "(newid(), 63, 5, u'" + id4 + "'), "
                            + "(newid(), 15, 6, u'" + id4 + "'), "
                            + "(newid(), 27, 7, u'" + id5 + "'), "
                            + "(newid(), 34, 8, u'" + id5 + "'), "
                            + "(newid(), 72, 9, u'" + id5 + "'), "
                            + "(newid(), 42, 2, u'" + id5 + "'), "
                            + "(newid(), 23, 3, u'" + id6 + "'), "
                            + "(newid(), 75, 5, u'" + id6 + "'), "
                            + "(newid(), 47, 7, u'" + id6 + "'), "
                            + "(newid(), 19, 8, u'" + id6 + "')");

                     Result rs = con.exec("select a, b, c, a*b, bin(a*c, 'a times c', 10, 25, 40, 75, 100, 200) from S order by a",
                                          new QueryParams().filter(
                                            new ComposableFilter(
                                              "S", "bin(a*c, 'a times c', 10, 25, 40, 75, 100, 200) != '02. 10 <= a times c < 25'"
                                            )
                                          ));
                     printResult(rs, 10);
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> ambiguousColumnFromJoinedTables() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     Assertions.assertThrows(AmbiguousColumnException.class, () ->
                         con.exec("select a, b, c, a*b, bin(a*c, 'a times c', 10, 25, 40, 75, 100, 200) " +
                                              "from S join a.b.T on s_id=S._id order by a"));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> binOnColumnFromJoinedTables() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete T from a.b.T");
                     con.exec("delete s from s:S");

                     UUID id1 = randomUUID(),
                          id2 = randomUUID(),
                          id3 = randomUUID(),
                          id4 = randomUUID(),
                          id5 = randomUUID(),
                          id6 = randomUUID();
                     con.exec("insert into S(_id, a, b, e, h, j, l) values "
                            + "(u'" + id1 + "', 1, 10, true, ['Four', 'Quatre']text, [1, 2, 3]int, 11),"
                            + "(u'" + id2 + "', 2, 21, true, ['Four', 'Quatre']text, [1, 2, 3]int, 22),"
                            + "(u'" + id3 + "', 3, 32, true, ['Four', 'Quatre']text, [1, 2, 3]int, 33),"
                            + "(u'" + id4 + "', 4, 24, true, ['Four', 'Quatre']text, [1, 2, 3]int, 24),"
                            + "(u'" + id5 + "', 5, 24, true, ['Four', 'Quatre']text, [1, 2, 3]int, 25),"
                            + "(u'" + id6 + "', 6, 71, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int, 76)");

                     con.exec("insert into a.b.T(_id, x, y, s_id) values"
                            + "(newid(), 11, 2, u'" + id1 + "'), "
                            + "(newid(), 25, 4, u'" + id1 + "'), "
                            + "(newid(), 36, 6, u'" + id1 + "'), "
                            + "(newid(), 73, 2, u'" + id2 + "'), "
                            + "(newid(), 62, 4, u'" + id2 + "'), "
                            + "(newid(), 55, 7, u'" + id2 + "'), "
                            + "(newid(), 37, 8, u'" + id2 + "'), "
                            + "(newid(), 76, 9, u'" + id3 + "'), "
                            + "(newid(), 93, 1, u'" + id3 + "'), "
                            + "(newid(), 82, 2, u'" + id3 + "'), "
                            + "(newid(), 63, 5, u'" + id4 + "'), "
                            + "(newid(), 15, 6, u'" + id4 + "'), "
                            + "(newid(), 27, 7, u'" + id5 + "'), "
                            + "(newid(), 34, 8, u'" + id5 + "'), "
                            + "(newid(), 72, 9, u'" + id5 + "'), "
                            + "(newid(), 42, 2, u'" + id5 + "'), "
                            + "(newid(), 23, 3, u'" + id6 + "'), "
                            + "(newid(), 75, 5, u'" + id6 + "'), "
                            + "(newid(), 47, 7, u'" + id6 + "'), "
                            + "(newid(), 19, 8, u'" + id6 + "')");

                     Result rs = con.exec("select S._id, l, x, y, l*x, y, l*x*y, bin(l*x*y, 'a times x', 10, 25, 40, 75, 100, 200, 1000, 10000) " +
                                          "from S join a.b.T on s_id=S._id order by l");
                     printResult(rs, 20);
                   }
                 }));
  }
}
