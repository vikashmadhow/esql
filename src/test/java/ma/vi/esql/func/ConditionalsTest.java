/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.func;

import ma.vi.esql.DataTest;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.exec.Result;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ConditionalsTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> ternaryExpression() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 2, 4, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 3, 6, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 4, 8, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Result rs = con.exec("""
                                          select sum(b     if a % 2 = 0 else
                                                     b * 2 if a % 3 = 0 else 0)
                                            from s:S""");
                     rs.toNext();
                     assertEquals(24, (Long)rs.value(1));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> ternaryExpressionAlt() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(newid(), 1, 2, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 2, 4, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 3, 6, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 4, 8, false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Result rs = con.exec("""
                                          select sum(a % 2 = 0 -> b     |
                                                     a % 3 = 0 -> b * 2 | 0)
                                            from s:S""");
                     rs.toNext();
                     assertEquals(24, (Long)rs.value(1));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> coalesce() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     con.exec("delete t from t:a.b.T");
                     con.exec("delete s from s:S");
                     con.exec("insert into S(_id, a, b, e, h, j) values "
                            + "(newid(), 1,    2,    true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), 3,    null, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), null, null, true, ['Four', 'Quatre']text, [1, 2, 3]int),"
                            + "(newid(), null, 8,    false, ['Nine', 'Neuf', 'X']text, [5, 6, 7, 8]int)");

                     Result rs = con.exec("""
                                          select sum(a?b?100)
                                            from s:S""");
                     rs.toNext();
                     assertEquals(112, (Long)rs.value(1));
                   }
                 }));
  }
}