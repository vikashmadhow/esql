/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.history;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.Result;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class FineHistoryTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> insertHistory() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("delete s from s:S$history");
                   }
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     String transId = con.transactionId();
                     LocalDateTime now = LocalDateTime.now();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                                  + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Result rs = con.exec("""
                                          select history_change_event, history_change_time, a, b
                                            from S$history
                                           where history_change_trans_id='""" + transId + "'" +
                                         " order by a");
                     assertTrue(rs.toNext());
                     assertEquals(rs.value(1), "I");
                     var span = Math.abs(Duration.between(now, rs.value(2)).toSeconds());
                     assertTrue(span <= 1);
                     assertEquals(1, (Integer)rs.value(3));
                     assertEquals(2, (Integer)rs.value(4));

                     assertTrue(rs.toNext());
                     assertEquals(rs.value(1), "I");
                     span = Math.abs(Duration.between(now, rs.value(2)).toSeconds());
                     assertTrue(span <= 1);
                     assertEquals(6, (Integer)rs.value(3));
                     assertEquals(7, (Integer)rs.value(4));

                     assertFalse(rs.toNext());
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> updateHistory() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("delete s from s:S$history");
                   }
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     String transId = con.transactionId();
                     LocalDateTime now = LocalDateTime.now();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                                  + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     con.exec("update S from S set a=a*2");

                     Result rs = con.exec("""
                                          select history_change_event, history_change_time, a, b
                                            from S$history
                                           where history_change_trans_id='""" + transId + "'" +
                                         " order by history_change_event, a");

                     assertTrue(rs.toNext());
                     assertEquals(rs.value(1), "F");
                     var span = Math.abs(Duration.between(now, rs.value(2)).toSeconds());
                     assertTrue(span <= 1);
                     assertEquals(1, (Integer)rs.value(3));
                     assertEquals(2, (Integer)rs.value(4));

                     assertTrue(rs.toNext());
                     assertEquals(rs.value(1), "F");
                     span = Math.abs(Duration.between(now, rs.value(2)).toSeconds());
                     assertTrue(span <= 1);
                     assertEquals(6, (Integer)rs.value(3));
                     assertEquals(7, (Integer)rs.value(4));

                     assertTrue(rs.toNext());
                     assertEquals(rs.value(1), "I");
                     span = Math.abs(Duration.between(now, rs.value(2)).toSeconds());
                     assertTrue(span <= 1);
                     assertEquals(1, (Integer)rs.value(3));
                     assertEquals(2, (Integer)rs.value(4));

                     assertTrue(rs.toNext());
                     assertEquals(rs.value(1), "I");
                     span = Math.abs(Duration.between(now, rs.value(2)).toSeconds());
                     assertTrue(span <= 1);
                     assertEquals(6, (Integer)rs.value(3));
                     assertEquals(7, (Integer)rs.value(4));

                     assertTrue(rs.toNext());
                     assertEquals(rs.value(1), "T");
                     span = Math.abs(Duration.between(now, rs.value(2)).toSeconds());
                     assertTrue(span <= 1);
                     assertEquals(2, (Integer)rs.value(3));
                     assertEquals(2, (Integer)rs.value(4));

                     assertTrue(rs.toNext());
                     assertEquals(rs.value(1), "T");
                     span = Math.abs(Duration.between(now, rs.value(2)).toSeconds());
                     assertTrue(span <= 1);
                     assertEquals(12, (Integer)rs.value(3));
                     assertEquals(7,  (Integer)rs.value(4));

                     assertFalse(rs.toNext());
                   }
                 }));
  }
}
