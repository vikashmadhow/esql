/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.parse;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.syntax.Parser;
import ma.vi.esql.syntax.query.Select;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static ma.vi.esql.syntax.Parser.Rules.SELECT;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ParseTest extends DataTest {

  @TestFactory
  Stream<DynamicTest> rangeAndConditionalTest() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete T from a.b.T");
                     con.exec("delete s from s:S");

                     UUID id1 = UUID.randomUUID(), id2 = UUID.randomUUID();
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                                  + "(u'" + id1 + "', 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                                  + "(u'" + id2 + "', 6, 7, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     con.exec("insert into a.b.T(_id, a, b, s_id) values"
                                  + "(newid(), 1, 2, u'" + id1 + "'), "
                                  + "(newid(), 3, 4, u'" + id2 + "')");

                     Parser p = new Parser(db.structure());
                     Select select = p.parse(
                         "select s.c if 0 < t.b < 5 else s.a, "
                             + "  'A' if t.b < 0 else "
                             + "  'B' if 0 <= t.b < 5 else "
                             + "  'C' if 5 <= t.b < 10 else 'D' "
                             + "  from t:a.b.T join s:S on t.s_id=s._id", SELECT);

                     con.exec(select);

//                     rs.next(); assertEquals(1,  (Integer)rs.value("a"));
//                                assertEquals(2,  (Integer)rs.value("b"));
//                                assertEquals(3,  (Integer)rs.value("c"));
//                     rs.next(); assertEquals(3,  (Integer)rs.value("a"));
//                                assertEquals(4,  (Integer)rs.value("b"));
//                                assertEquals(13, (Integer)rs.value("c"));
                   }
                 }));
  }
}
