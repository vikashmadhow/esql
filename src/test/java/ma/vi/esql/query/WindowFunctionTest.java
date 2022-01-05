/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.query;

import ma.vi.esql.DataTest;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class WindowFunctionTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> windowSum() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete T from a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("""
                              insert into S(_id, a, b, e, h, i)
                              values(newid(), 1, 12, true,  default, default),
                                    (newid(), 1, 10, false, default, default),
                                    (newid(), 2,  8, true,  default, default),
                                    (newid(), 2,  6, false, default, default),
                                    (newid(), 3,  4, true,  default, default),
                                    (newid(), 3,  2, false, default, default)""");

                     Result rs = con.exec("""
                         select a, b, s:sum(b) over(partition by a)
                           from S
                          order by b desc
                         """);

                     rs.next(); assertEquals(1,  (Integer)rs.value("a"));
                                assertEquals(12, (Integer)rs.value("b"));
                                assertEquals(22, (Long)rs.value("s"));
                     rs.next(); assertEquals(1,  (Integer)rs.value("a"));
                                assertEquals(10, (Integer)rs.value("b"));
                                assertEquals(22, (Long)rs.value("s"));

                     rs.next(); assertEquals(2,  (Integer)rs.value("a"));
                                assertEquals(8,  (Integer)rs.value("b"));
                                assertEquals(14, (Long)rs.value("s"));
                     rs.next(); assertEquals(2,  (Integer)rs.value("a"));
                                assertEquals(6,  (Integer)rs.value("b"));
                                assertEquals(14, (Long)rs.value("s"));

                     rs.next(); assertEquals(3,  (Integer)rs.value("a"));
                                assertEquals(4,  (Integer)rs.value("b"));
                                assertEquals(6,  (Long)rs.value("s"));
                     rs.next(); assertEquals(3,  (Integer)rs.value("a"));
                                assertEquals(2,  (Integer)rs.value("b"));
                                assertEquals(6,  (Long)rs.value("s"));
                   }
                 }));
  }
}
