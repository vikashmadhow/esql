/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.trans;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.Database;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.ResultColumn;
import ma.vi.esql.syntax.Parser;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class TransactionTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> threadLocalBoundary() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql()) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                                  + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     sameThreadCon(db, con);
                     newThreadCon(db, con);
                   }
                 }));
  }

  void sameThreadCon(Database db, EsqlConnection previousCon) {
    EsqlConnection con = db.esql();
    assertSame(previousCon.con(), con.con());
    try (Result rs = con.exec("select a, b from s:S order by s.a asc")) {
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
  }

  void newThreadCon(Database db, EsqlConnection previousCon) {
    new Thread(() -> {
      EsqlConnection con = db.esql();
      assertNotSame(previousCon.con(), con.con());
      try (Result rs = con.exec("select count(*) from s:S")) {
        rs.toNext();
        assertEquals(0L, (Long)rs.value(1));
      }
    }).start();
  }
}
