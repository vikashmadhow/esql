/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.DataTest;
import ma.vi.esql.exec.EsqlConnection;
import ma.vi.esql.exec.Result;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class WithTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> simpleWithSelect() {
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
                                  + "(newid(), 3, 4, u'" + id2 + "')");

                     Result rs = con.exec(
                         "with s(id, a, b, c) ("
                       + "  select _id, a, b, c from S order by a"
                       + ")"
                       + "select t.a, t.b, s.c "
                       + "  from t:a.b.T join s on t.s_id=s.id");

                     rs.next(); assertEquals(1, (Integer)rs.value("a"));
                                assertEquals(2, (Integer)rs.value("b"));
                                assertEquals(3, (Integer)rs.value("c"));
                     rs.next(); assertEquals(3, (Integer)rs.value("a"));
                                assertEquals(4, (Integer)rs.value("b"));
                                assertEquals(13, (Integer)rs.value("c"));
                   }
                 }));
  }

}
