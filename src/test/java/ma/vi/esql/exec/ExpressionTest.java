/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.exec;

import ma.vi.esql.DataTest;
import ma.vi.esql.syntax.Parser;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static ma.vi.esql.syntax.expression.literal.DateLiteral.date;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ExpressionTest extends DataTest {
  @TestFactory
  Stream<DynamicTest> execLiterals() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     assertEquals(1,     (Long)con.exec(p.parse("1")));
                     assertEquals(-1,    (Long)con.exec(p.parse("-1")));

                     assertEquals(1.5,  con.exec(p.parse("1.5")));
                     assertEquals(-1.5,  con.exec(p.parse("-1.5")));
                     assertEquals(1.5e10,  con.exec(p.parse("1.5e10")));
                     assertEquals(1.5e-10,  con.exec(p.parse("1.5e-10")));
                     assertEquals(-1.5e10,  con.exec(p.parse("-1.5e10")));
                     assertEquals(-1.5e-10,  con.exec(p.parse("-1.5e-10")));

                     assertEquals("abc", con.exec(p.parse("'abc'")));
                     assertEquals("""
                                  function sum(x, y) {
                                    x *= x''s;
                                    y += x;
                                    if (x > y) {
                                      return y;
                                    } else {
                                      return x - y;
                                    }
                                  }""",
                                  con.exec(p.parse("""
                                   `
                                   function sum(x, y) {
                                     x *= x's;
                                     y += x;
                                     if (x > y) {
                                       return y;
                                     } else {
                                       return x - y;
                                     }
                                   }
                                   `
                                   """)));

                     assertEquals(date(2022, 1, 1),
                                  con.exec(p.parse("d'2022-01-01'")));

                     assertEquals(date(2022, 1, 1, 9, 31, 2, 0),
                                  con.exec(p.parse("d'2022-01-01 09:31:02'")));

                     assertEquals(date(2022, 1, 1, 9, 31, 2, 5),
                                  con.exec(p.parse("d'2022-01-01 9:31:2.5'")));

                     assertNull(con.exec(p.parse("null")));

                     assertTrue((Boolean)con.exec(p.parse("true")));
                     assertFalse((Boolean)con.exec(p.parse("false")));

                     assertEquals(UUID.fromString("01234567-89AB-CDEF-0123-456789ABCDEF"),
                                  con.exec(p.parse("u'01234567-89AB-CDEF-0123-456789ABCDEF'")));
                   }
                 }));
  }

  @TestFactory
  Stream<DynamicTest> execArithmeticExpressions() {
    return Stream.of(databases)
                 .map(db -> dynamicTest(db.target().toString(), () -> {
                   Parser p = new Parser(db.structure());
                   try (EsqlConnection con = db.esql(db.pooledConnection())) {
                     assertEquals(2, (Long)con.exec(p.parse("1+1")));
                     assertEquals(0, (Long)con.exec(p.parse("1-1")));
                     assertEquals(0, (Long)con.exec(p.parse("-1+1")));
                     assertEquals(-3, (Long)con.exec(p.parse("-1-1-1")));
                     assertEquals(-1, (Long)con.exec(p.parse("1+-1-1")));

                     assertEquals(0, (Long)con.exec(p.parse("1*0")));
                     assertEquals(1, (Long)con.exec(p.parse("-1*-1")));
                     assertEquals(17, (Long)con.exec(p.parse("2 + 3 * 5")));

                     assertEquals(2., con.exec(p.parse("10 / 5")));
                     assertEquals(5.1, con.exec(p.parse("10.2 / 2")));

                     assertEquals(2, (Long)con.exec(p.parse("7 % 5")));
//                     assertEquals(0.2, con.exec(p.parse("10.2 % 2")));

                     assertEquals(256., con.exec(p.parse("2 ^ 2 ^ 3")));
                   }
                 }));
  }
}
