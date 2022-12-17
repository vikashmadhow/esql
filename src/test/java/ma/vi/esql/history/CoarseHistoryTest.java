/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.history;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.Result;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
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
                     LocalDateTime now = LocalDateTime.now();
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

  @TestFactory
  Stream<DynamicTest> updateHistory() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                   }
                   String transId;
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     transId = con.transactionId();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                                  + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     con.exec("update S from S set b=b+1 where a=1");
                     con.exec("delete S from S where a=6");
                   }

                   System.out.println(transId);
                   try (EsqlConnection con = db.esql()) {
                     int i = 1;
                     while(i < 10
                       && !((Result)con.exec("""
                                     select table_name, event
                                       from _core.history
                                      where trans_id='""" + transId + "'"
                                  + " order by event")).toNext()) {
                       Thread.sleep(100);
                       i++;
                     }
                     Result rs = con.exec("""
                                          select table_name, event
                                            from _core.history
                                           where trans_id='""" + transId + "'"
                                       + " order by event");
                     matchResult(rs, List.of(Map.of("table_name", "S", "event", "D"),
                                             Map.of("table_name", "S", "event", "I"),
                                             Map.of("table_name", "S", "event", "U")));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> coarseHistoryNotification() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                   }
                   Database.Subscription subscription = db.subscribe("S");
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                                + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     con.exec("update S from S set b=b+1 where a=1");
                     con.exec("delete S from S where a=6");
                   }
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                                + "(newid(), 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");
                   }

                   Database.ChangeEvent e = subscription.events().take();
                   assertEquals(e.table(), "S");
                   assertTrue(e.hasDeletes());
                   assertTrue(e.hasInserts());
                   assertTrue(e.hasUpdates());

                   e = subscription.events().take();
                   assertEquals(e.table(), "S");
                   assertFalse(e.hasDeletes());
                   assertTrue(e.hasInserts());
                   assertFalse(e.hasUpdates());
                 }));
  }
}
