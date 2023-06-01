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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class CheckDigitTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> checkdigit() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   System.out.println(db.target());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     long o = 123456789012L;
                     Result rs = con.exec("select checkdigit(" + o + ")");
                     rs.toNext();
                     long v = rs.value(1);
                     System.out.println(v);
                     assertEquals(1234567890128L, v);
                     assertFalse(rs.toNext());
                   }
                 }));
  }
}