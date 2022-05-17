/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.history;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.Result;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class CoarseHistoryTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> insertHistory() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                   }
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     String transId = con.transactionId();
                     Date now = new Date();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                                  + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Result rs = con.exec("select event, at_time from _core._temp_history where trans_id='" + transId + "'");
                     rs.toNext();

                     System.out.println(now);
                     System.out.println(rs.value(2).toString());
                     System.out.println(now.compareTo(rs.value(2)));

                     assertEquals(rs.value(1), "I");
                     // assertTrue(now.compareTo(rs.value(2)) <= 0);
                     assertFalse(rs.toNext());
                   }
                 }));
  }
}
